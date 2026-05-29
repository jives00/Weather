package com.weather.app.ui.main

import android.app.Application
import android.location.Geocoder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.weather.app.data.datastore.LocationDataStore
import com.weather.app.data.datastore.SettingsDataStore
import com.weather.app.data.datastore.WidgetDataStore
import com.weather.app.data.repository.WeatherRepository
import com.weather.app.domain.model.Units
import com.weather.app.domain.model.WeatherForecast
import com.weather.app.domain.model.WeatherLocation
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale

sealed class WeatherUiState {
    data object Loading : WeatherUiState()
    data class Success(val forecast: WeatherForecast) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val locationDataStore = LocationDataStore(application)
    private val settingsDataStore = SettingsDataStore(application)
    private val repository = WeatherRepository()
    private val fusedLocation = LocationServices.getFusedLocationProviderClient(application)

    val units: StateFlow<Units> = settingsDataStore.units
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Units.IMPERIAL)

    private val savedLocations: StateFlow<List<WeatherLocation>> = locationDataStore.locations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _gpsLocation = MutableStateFlow<WeatherLocation?>(null)

    val locations: StateFlow<List<WeatherLocation>> = combine(_gpsLocation, savedLocations) { gps, saved ->
        buildList {
            gps?.let { add(it) }
            addAll(saved)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _forecasts = MutableStateFlow<Map<String, WeatherUiState>>(emptyMap())
    val forecasts: StateFlow<Map<String, WeatherUiState>> = _forecasts.asStateFlow()

    private val _locationPermissionGranted = MutableStateFlow(false)

    fun onLocationPermissionGranted() {
        _locationPermissionGranted.value = true
        refreshGpsLocation()
    }

    fun refreshGpsLocation() {
        viewModelScope.launch {
            try {
                @Suppress("MissingPermission")
                val loc = fusedLocation.lastLocation.await() ?: return@launch
                val name = resolveLocationName(loc.latitude, loc.longitude)
                val gps = WeatherLocation(id = "gps", name = name, latitude = loc.latitude, longitude = loc.longitude, isCurrentLocation = true)
                _gpsLocation.value = gps
                fetchForecast(gps)
            } catch (_: Exception) {}
        }
    }

    fun refreshAll() {
        viewModelScope.launch {
            locations.value.forEach { fetchForecast(it) }
        }
    }

    fun fetchForecast(location: WeatherLocation) {
        viewModelScope.launch {
            _forecasts.update { it + (location.id to WeatherUiState.Loading) }
            val result = repository.getForecast(location, units.value)
            _forecasts.update { map ->
                map + (location.id to result.fold(
                    onSuccess = { forecast ->
                        WidgetDataStore.save(getApplication(), forecast)
                        WeatherUiState.Success(forecast)
                    },
                    onFailure = { WeatherUiState.Error(it.message ?: "Failed to load weather") }
                ))
            }
        }
    }

    fun addLocation(location: WeatherLocation) {
        viewModelScope.launch {
            locationDataStore.saveLocation(location)
            fetchForecast(location)
        }
    }

    fun removeLocation(locationId: String) {
        viewModelScope.launch {
            locationDataStore.removeLocation(locationId)
            _forecasts.update { it - locationId }
        }
    }

    fun searchLocations(query: String): List<WeatherLocation> {
        return try {
            @Suppress("DEPRECATION")
            val addresses = Geocoder(getApplication(), Locale.getDefault())
                .getFromLocationName(query, 5) ?: emptyList()
            addresses.mapNotNull { addr ->
                val name = addr.locality ?: addr.subAdminArea ?: addr.adminArea ?: return@mapNotNull null
                val country = if (addr.countryCode != "US") ", ${addr.countryCode}" else ""
                WeatherLocation(
                    name = "$name$country",
                    latitude = addr.latitude,
                    longitude = addr.longitude
                )
            }
        } catch (_: Exception) { emptyList() }
    }

    private fun resolveLocationName(lat: Double, lon: Double): String {
        return try {
            @Suppress("DEPRECATION")
            val addresses = Geocoder(getApplication(), Locale.getDefault()).getFromLocation(lat, lon, 1)
            addresses?.firstOrNull()?.locality
                ?: addresses?.firstOrNull()?.subAdminArea
                ?: "Current Location"
        } catch (_: Exception) { "Current Location" }
    }
}
