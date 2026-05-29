package com.weather.app.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.weather.app.data.datastore.LocationDataStore
import com.weather.app.data.datastore.SettingsDataStore
import com.weather.app.domain.model.Units
import com.weather.app.domain.model.WeatherLocation
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsDataStore = SettingsDataStore(application)
    private val locationDataStore = LocationDataStore(application)

    val units: StateFlow<Units> = settingsDataStore.units
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Units.IMPERIAL)

    val savedLocations: StateFlow<List<WeatherLocation>> = locationDataStore.locations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setUnits(units: Units) {
        viewModelScope.launch { settingsDataStore.setUnits(units) }
    }

    fun removeLocation(locationId: String) {
        viewModelScope.launch { locationDataStore.removeLocation(locationId) }
    }

    fun reorderLocations(locations: List<WeatherLocation>) {
        viewModelScope.launch { locationDataStore.reorderLocations(locations) }
    }
}
