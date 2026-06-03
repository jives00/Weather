package com.weather.app.work

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.weather.app.data.datastore.LocationDataStore
import com.weather.app.data.datastore.SettingsDataStore
import com.weather.app.data.datastore.WidgetDataStore
import com.weather.app.data.repository.WeatherRepository
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
        private const val TAG = "WeatherRefresh"
    }

    private val repository = WeatherRepository()
    private val locationDataStore = LocationDataStore(context)
    private val settingsDataStore = SettingsDataStore(context)

    override suspend fun doWork(): Result {
        Log.d(TAG, "doWork() started")
        return try {
            val units = settingsDataStore.units.firstOrNull() ?: run {
                Log.w(TAG, "No units found, skipping refresh")
                return Result.success()
            }
            val gpsLocation = locationDataStore.currentLocation.firstOrNull()
            val savedLocations = locationDataStore.locations.firstOrNull() ?: emptyList()
            val allLocations = buildList {
                gpsLocation?.let { add(it) }
                addAll(savedLocations)
            }
            Log.d(TAG, "Refreshing ${allLocations.size} location(s) (gps=${gpsLocation != null}, saved=${savedLocations.size})")

            allLocations.forEach { location ->
                repository.getForecast(location, units)
                    .onSuccess { forecast ->
                        Log.d(TAG, "Fetch success for ${location.name}: ${forecast.current.temperature}°")
                        WidgetDataStore.save(context, forecast)
                    }
                    .onFailure { e ->
                        Log.e(TAG, "Fetch failed for ${location.name}: ${e.message}")
                    }
            }

            Log.d(TAG, "Calling updateAll() on all widgets")
            SmallWeatherWidget().updateAll(context)
            BarWeatherWidget().updateAll(context)
            MediumWeatherWidget().updateAll(context)
            LargeWeatherWidget().updateAll(context)

            Log.d(TAG, "doWork() complete")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "doWork() threw exception: ${e.message}", e)
            Result.retry()
        }
    }
}
