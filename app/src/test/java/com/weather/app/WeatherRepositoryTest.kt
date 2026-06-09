package com.weather.app

import com.weather.app.data.api.AirQualityApi
import com.weather.app.data.api.AirQualityCurrentData
import com.weather.app.data.api.AirQualityResponse
import com.weather.app.data.api.CurrentData
import com.weather.app.data.api.DailyData
import com.weather.app.data.api.HourlyData
import com.weather.app.data.api.WeatherApi
import com.weather.app.data.api.WeatherApiResponse
import com.weather.app.data.repository.WeatherRepository
import com.weather.app.domain.model.Units
import com.weather.app.domain.model.WeatherCondition
import com.weather.app.domain.model.WeatherLocation
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class WeatherRepositoryTest {
    private lateinit var weatherApi: WeatherApi
    private lateinit var airQualityApi: AirQualityApi
    private lateinit var repository: WeatherRepository

    private val testLocation = WeatherLocation(
        id = "test", name = "Chicago", latitude = 41.88, longitude = -87.63
    )

    @Before
    fun setup() {
        weatherApi = mockk()
        airQualityApi = mockk()
        repository = WeatherRepository(weatherApi, airQualityApi)
    }

    @Test
    fun `getForecast returns success with correct temperature`() = runTest {
        coEvery {
            weatherApi.getForecast(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())
        } returns fakeResponse()

        coEvery {
            airQualityApi.getAirQuality(any(), any(), any())
        } returns AirQualityResponse(AirQualityCurrentData(42))

        val result = repository.getForecast(testLocation, Units.IMPERIAL)
        assertTrue(result.isSuccess)
        val forecast = result.getOrThrow()
        assertEquals(72.0, forecast.current.temperature, 0.01)
        assertEquals(42, forecast.aqi)
    }

    @Test
    fun `getForecast returns failure when api throws`() = runTest {
        coEvery {
            weatherApi.getForecast(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())
        } throws RuntimeException("Network error")

        val result = repository.getForecast(testLocation, Units.IMPERIAL)
        assertTrue(result.isFailure)
    }

    @Test
    fun `getForecast succeeds even when air quality api fails`() = runTest {
        coEvery {
            weatherApi.getForecast(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())
        } returns fakeResponse()

        coEvery {
            airQualityApi.getAirQuality(any(), any(), any())
        } throws RuntimeException("AQI unavailable")

        val result = repository.getForecast(testLocation, Units.IMPERIAL)
        assertTrue(result.isSuccess)
        assertNull(result.getOrThrow().aqi)
    }

    @Test
    fun `getForecast correctly uses imperial units in api call`() = runTest {
        coEvery {
            weatherApi.getForecast(
                latitude = any(), longitude = any(), current = any(), hourly = any(), daily = any(),
                forecastDays = any(), forecastHours = any(),
                temperatureUnit = "fahrenheit", windSpeedUnit = "mph", precipitationUnit = "inch", timezone = any()
            )
        } returns fakeResponse()
        coEvery { airQualityApi.getAirQuality(any(), any(), any()) } returns AirQualityResponse(AirQualityCurrentData(null))

        val result = repository.getForecast(testLocation, Units.IMPERIAL)
        assertTrue(result.isSuccess)
    }

    private fun fakeResponse() = WeatherApiResponse(
        latitude = 41.88, longitude = -87.63, timezone = "America/Chicago",
        current = CurrentData(
            temperature = 72.0, relativeHumidity = 55, apparentTemperature = 70.0,
            isDay = 1, weatherCode = 1, windSpeed = 10.0, windDirection = 180,
            surfacePressure = 1013.0, visibility = 10000.0
        ),
        hourly = HourlyData(
            time = listOf("2024-01-01T00:00", "2024-01-01T01:00"),
            temperature = listOf(72.0, 71.0),
            precipitationProbability = listOf(10, 15),
            precipitation = listOf(0.0, 0.0),
            weatherCode = listOf(1, 1),
            isDay = listOf(1, 0)
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
