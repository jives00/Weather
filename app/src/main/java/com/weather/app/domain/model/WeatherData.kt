package com.weather.app.domain.model

data class CurrentWeather(
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val condition: WeatherCondition,
    val conditionDescription: String,
    val windSpeed: Double,
    val windDirection: Int,
    val uvIndex: Double,
    val visibility: Double,
    val pressure: Double,
    val isDay: Boolean,
    val wmoCode: Int
)

data class HourlyWeather(
    val time: Long,
    val temperature: Double,
    val precipitationProbability: Int,
    val condition: WeatherCondition,
    val wmoCode: Int,
    val isDay: Boolean
)

data class DailyWeather(
    val time: Long,
    val tempMax: Double,
    val tempMin: Double,
    val condition: WeatherCondition,
    val wmoCode: Int,
    val sunrise: Long,
    val sunset: Long,
    val precipitationProbability: Int,
    val precipitationSum: Double,
    val windSpeedMax: Double,
    val uvIndexMax: Double
)

data class WeatherForecast(
    val location: WeatherLocation,
    val current: CurrentWeather,
    val hourly: List<HourlyWeather>,
    val daily: List<DailyWeather>,
    val highToday: Double,
    val lowToday: Double,
    val aqi: Int?,
    val units: Units,
    val fetchedAt: Long = System.currentTimeMillis()
)
