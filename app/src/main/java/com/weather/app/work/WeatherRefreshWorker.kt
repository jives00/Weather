package com.weather.app.work

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.weather.app.data.datastore.LocationDataStore
import com.weather.app.data.datastore.SettingsDataStore
import com.weather.app.data.datastore.WidgetDataStore
import com.weather.app.data.repository.WeatherRepository
import com.weather.app.domain.model.WeatherLocation
import com.weather.app.widget.BarWeatherWidget
import com.weather.app.widget.LargeWeatherWidget
import com.weather.app.widget.MediumWeatherWidget
import com.weather.app.widget.SmallWeatherWidget
import kotlinx.coroutines.flow.firstOrNull

class WeatherRefreshWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "weather_refresh"
    }

    private val repository = WeatherRepository()
    private val locationDataStore = LocationDataStore(context)
    private val settingsDataStore = SettingsDataStore(context)

    override suspend fun doWork(): Result {
        return try {
            val units = settingsDataStore.units.firstOrNull() ?: return Result.success()
            val savedLocations = locationDataStore.locations.firstOrNull() ?: emptyList()

            // Also refresh GPS location if we have a cached one
            val primaryData = WidgetDataStore.getPrimary(context)
            val allLocations: List<WeatherLocation> = buildList {
                if (primaryData != null) {
                    val cached = WidgetDataStore.get(context, "gps")
                    if (cached != null) {
                        add(WeatherLocation(id = "gps", name = cached.locationName, latitude = 0.0, longitude = 0.0, isCurrentLocation = true))
                    }
                }
                addAll(savedLocations)
            }

            savedLocations.forEach { location ->
                repository.getForecast(location, units).onSuccess { forecast ->
                    WidgetDataStore.save(context, forecast)
                }
            }

            // Update all widget instances
            SmallWeatherWidget().updateAll(context)
            BarWeatherWidget().updateAll(context)
            MediumWeatherWidget().updateAll(context)
            LargeWeatherWidget().updateAll(context)

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
