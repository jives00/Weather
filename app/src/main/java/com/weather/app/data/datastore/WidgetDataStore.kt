package com.weather.app.data.datastore

import android.content.Context
import com.google.gson.Gson
import com.weather.app.domain.model.WeatherCondition
import com.weather.app.domain.model.WeatherForecast

data class WidgetData(
    val locationId: String,
    val locationName: String,
    val temperature: Int,
    val conditionName: String,
    val conditionDescription: String,
    val highTemp: Int,
    val lowTemp: Int,
    val feelsLike: Int,
    val hourlyTemps: List<Int>,
    val hourlyConditions: List<String>,
    val temperatureSymbol: String,
    val isDay: Boolean,
    val lastRefreshedAt: Long = 0L
) {
    val condition: WeatherCondition get() = runCatching { WeatherCondition.valueOf(conditionName) }.getOrDefault(WeatherCondition.UNKNOWN)
}

object WidgetDataStore {
    private const val PREFS_NAME = "widget_data"
    private const val PRIMARY_KEY = "primary_location_id"
    private val gson = Gson()

    fun save(context: Context, forecast: WeatherForecast) {
        val now = System.currentTimeMillis() / 1000
        val upcoming = forecast.hourly.filter { it.time >= now }.take(4)
        val data = WidgetData(
            locationId = forecast.location.id,
            locationName = forecast.location.name,
            temperature = forecast.current.temperature.toInt(),
            conditionName = forecast.current.condition.name,
            conditionDescription = forecast.current.conditionDescription,
            highTemp = forecast.highToday.toInt(),
            lowTemp = forecast.lowToday.toInt(),
            feelsLike = forecast.current.feelsLike.toInt(),
            hourlyTemps = upcoming.map { it.temperature.toInt() },
            hourlyConditions = upcoming.map { it.condition.name },
            temperatureSymbol = forecast.units.temperatureSymbol,
            isDay = forecast.current.isDay,
            lastRefreshedAt = System.currentTimeMillis()
        )
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString("weather_${forecast.location.id}", gson.toJson(data))
            .apply()

        // Track primary (first/GPS) location for single-slot widgets
        if (forecast.location.isCurrentLocation || prefs.getString(PRIMARY_KEY, null) == null) {
            prefs.edit().putString(PRIMARY_KEY, forecast.location.id).apply()
        }
    }

    fun getPrimary(context: Context): WidgetData? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val primaryId = prefs.getString(PRIMARY_KEY, null)
            ?: prefs.all.keys.firstOrNull { it.startsWith("weather_") }?.removePrefix("weather_")
            ?: return null
        return get(context, primaryId)
    }

    fun get(context: Context, locationId: String): WidgetData? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString("weather_$locationId", null) ?: return null
        return runCatching { gson.fromJson(json, WidgetData::class.java) }.getOrNull()
    }
}
