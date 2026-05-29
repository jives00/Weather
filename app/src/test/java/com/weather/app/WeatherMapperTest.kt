package com.weather.app

import com.weather.app.data.api.CurrentData
import com.weather.app.data.api.DailyData
import com.weather.app.data.api.HourlyData
import com.weather.app.data.api.WeatherApiResponse
import com.weather.app.domain.mapper.WeatherMapper
import com.weather.app.domain.model.Units
import com.weather.app.domain.model.WeatherCondition
import com.weather.app.domain.model.WeatherLocation
import org.junit.Assert.*
import org.junit.Test

class WeatherMapperTest {
    private val location = WeatherLocation(id = "test", name = "Chicago", latitude = 41.88, longitude = -87.63)

    @Test
    fun `mapper extracts today high and low from daily data`() {
        val forecast = WeatherMapper.mapToForecast(fakeResponse(), location, Units.IMPERIAL, null)
        assertEquals(78.0, forecast.highToday, 0.01)
        assertEquals(60.0, forecast.lowToday, 0.01)
    }

    @Test
    fun `mapper extracts UV index from daily data into current weather`() {
        val forecast = WeatherMapper.mapToForecast(fakeResponse(), location, Units.IMPERIAL, null)
        assertEquals(3.0, forecast.current.uvIndex, 0.01)
    }

    @Test
    fun `mapper passes through AQI`() {
        val forecast = WeatherMapper.mapToForecast(fakeResponse(), location, Units.IMPERIAL, 55)
        assertEquals(55, forecast.aqi)
    }

    @Test
    fun `mapper maps hourly data correctly`() {
        val forecast = WeatherMapper.mapToForecast(fakeResponse(), location, Units.IMPERIAL, null)
        assertEquals(2, forecast.hourly.size)
        assertEquals(72.0, forecast.hourly[0].temperature, 0.01)
        assertEquals(10, forecast.hourly[0].precipitationProbability)
    }

    @Test
    fun `mapper maps daily data correctly`() {
        val forecast = WeatherMapper.mapToForecast(fakeResponse(), location, Units.IMPERIAL, null)
        assertEquals(2, forecast.daily.size)
        assertEquals(78.0, forecast.daily[0].tempMax, 0.01)
        assertEquals(60.0, forecast.daily[0].tempMin, 0.01)
    }

    @Test
    fun `mapper sets is_day correctly on current weather`() {
        val forecast = WeatherMapper.mapToForecast(fakeResponse(), location, Units.IMPERIAL, null)
        assertTrue(forecast.current.isDay)
    }

    private fun fakeResponse() = WeatherApiResponse(
        latitude = 41.88, longitude = -87.63, timezone = "America/Chicago",
        current = CurrentData(72.0, 55, 70.0, isDay = 1, weatherCode = 1, 10.0, 180, 1013.0, 10000.0),
        hourly = HourlyData(
            time = listOf("2024-01-01T12:00", "2024-01-01T13:00"),
            temperature = listOf(72.0, 73.0),
            precipitationProbability = listOf(10, 15),
            weatherCode = listOf(1, 1),
            isDay = listOf(1, 1)
        ),
        daily = DailyData(
            time = listOf("2024-01-01", "2024-01-02"),
            weatherCode = listOf(1, 2),
            temperatureMax = listOf(78.0, 75.0),
            temperatureMin = listOf(60.0, 58.0),
            sunrise = listOf("2024-01-01T07:00", "2024-01-02T07:01"),
            sunset = listOf("2024-01-01T17:30", "2024-01-02T17:31"),
            precipitationSum = listOf(0.0, 0.1),
            precipitationProbabilityMax = listOf(10, 30),
            windSpeedMax = listOf(15.0, 12.0),
            uvIndexMax = listOf(3.0, 4.0)
        )
    )
}
