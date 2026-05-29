package com.weather.app.data.api

import com.google.gson.annotations.SerializedName

data class WeatherApiResponse(
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val current: CurrentData,
    val hourly: HourlyData,
    val daily: DailyData
)

data class CurrentData(
    @SerializedName("temperature_2m") val temperature: Double,
    @SerializedName("relative_humidity_2m") val relativeHumidity: Int,
    @SerializedName("apparent_temperature") val apparentTemperature: Double,
    @SerializedName("is_day") val isDay: Int,
    @SerializedName("weather_code") val weatherCode: Int,
    @SerializedName("wind_speed_10m") val windSpeed: Double,
    @SerializedName("wind_direction_10m") val windDirection: Int,
    @SerializedName("surface_pressure") val surfacePressure: Double,
    @SerializedName("visibility") val visibility: Double
)

data class HourlyData(
    val time: List<String>,
    @SerializedName("temperature_2m") val temperature: List<Double>,
    @SerializedName("precipitation_probability") val precipitationProbability: List<Int?>,
    @SerializedName("precipitation") val precipitation: List<Double?>,
    @SerializedName("weather_code") val weatherCode: List<Int>,
    @SerializedName("is_day") val isDay: List<Int>
)

data class DailyData(
    val time: List<String>,
    @SerializedName("weather_code") val weatherCode: List<Int>,
    @SerializedName("temperature_2m_max") val temperatureMax: List<Double>,
    @SerializedName("temperature_2m_min") val temperatureMin: List<Double>,
    val sunrise: List<String>,
    val sunset: List<String>,
    @SerializedName("precipitation_sum") val precipitationSum: List<Double>,
    @SerializedName("precipitation_probability_max") val precipitationProbabilityMax: List<Int?>,
    @SerializedName("wind_speed_10m_max") val windSpeedMax: List<Double>,
    @SerializedName("uv_index_max") val uvIndexMax: List<Double>
)

data class AirQualityResponse(
    val current: AirQualityCurrentData
)

data class AirQualityCurrentData(
    @SerializedName("us_aqi") val usAqi: Int?
)
