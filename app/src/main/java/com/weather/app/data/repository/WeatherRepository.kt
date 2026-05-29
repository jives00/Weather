package com.weather.app.data.repository

import com.weather.app.data.api.WeatherApiClient
import com.weather.app.domain.mapper.WeatherMapper
import com.weather.app.domain.model.Units
import com.weather.app.domain.model.WeatherForecast
import com.weather.app.domain.model.WeatherLocation

class WeatherRepository(
    private val weatherApi: com.weather.app.data.api.WeatherApi = WeatherApiClient.weatherApi,
    private val airQualityApi: com.weather.app.data.api.AirQualityApi = WeatherApiClient.airQualityApi
) {
    suspend fun getForecast(location: WeatherLocation, units: Units): Result<WeatherForecast> {
        return try {
            val response = weatherApi.getForecast(
                latitude = location.latitude,
                longitude = location.longitude,
                temperatureUnit = units.apiTemperatureUnit,
                windSpeedUnit = units.apiWindSpeedUnit,
                precipitationUnit = units.apiPrecipitationUnit
            )
            val aqi = runCatching {
                airQualityApi.getAirQuality(location.latitude, location.longitude).current.usAqi
            }.getOrNull()

            Result.success(WeatherMapper.mapToForecast(response, location, units, aqi))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
