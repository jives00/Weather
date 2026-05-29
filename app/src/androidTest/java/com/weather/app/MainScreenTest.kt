package com.weather.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit4.runners.AndroidJUnit4
import com.weather.app.domain.model.CurrentWeather
import com.weather.app.domain.model.Units
import com.weather.app.domain.model.WeatherCondition
import com.weather.app.domain.model.WeatherForecast
import com.weather.app.domain.model.WeatherLocation
import com.weather.app.ui.components.DetailCardsGrid
import com.weather.app.ui.components.DailyForecastList
import com.weather.app.ui.components.HourlyForecastRow
import com.weather.app.ui.theme.WeatherTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainScreenTest {
    @get:Rule val composeRule = createComposeRule()

    private val fakeLocation = WeatherLocation(id = "test", name = "Chicago", latitude = 41.88, longitude = -87.63)
    private val fakeCurrentWeather = CurrentWeather(
        temperature = 72.0, feelsLike = 70.0, humidity = 55, condition = WeatherCondition.CLEAR_DAY,
        conditionDescription = "Clear sky", windSpeed = 10.0, windDirection = 180,
        uvIndex = 3.0, visibility = 10000.0, pressure = 1013.0, isDay = true, wmoCode = 0
    )
    private val fakeForecast = WeatherForecast(
        location = fakeLocation, current = fakeCurrentWeather, hourly = emptyList(),
        daily = emptyList(), highToday = 78.0, lowToday = 60.0, aqi = 42, units = Units.IMPERIAL
    )

    @Test
    fun detailCards_displayAllRequiredFields() {
        composeRule.setContent {
            WeatherTheme { DetailCardsGrid(forecast = fakeForecast) }
        }
        composeRule.onNodeWithText("Humidity").assertIsDisplayed()
        composeRule.onNodeWithText("Wind").assertIsDisplayed()
        composeRule.onNodeWithText("UV Index").assertIsDisplayed()
        composeRule.onNodeWithText("Visibility").assertIsDisplayed()
        composeRule.onNodeWithText("Pressure").assertIsDisplayed()
        composeRule.onNodeWithText("Feels Like").assertIsDisplayed()
        composeRule.onNodeWithText("Air Quality").assertIsDisplayed()
    }

    @Test
    fun detailCards_humidityValueDisplayed() {
        composeRule.setContent {
            WeatherTheme { DetailCardsGrid(forecast = fakeForecast) }
        }
        composeRule.onNodeWithText("55%").assertIsDisplayed()
    }

    @Test
    fun detailCards_aqiDisplayed() {
        composeRule.setContent {
            WeatherTheme { DetailCardsGrid(forecast = fakeForecast) }
        }
        composeRule.onNodeWithText("42").assertIsDisplayed()
    }

    @Test
    fun detailCards_noAqiWhenNull() {
        val forecastNoAqi = fakeForecast.copy(aqi = null)
        composeRule.setContent {
            WeatherTheme { DetailCardsGrid(forecast = forecastNoAqi) }
        }
        composeRule.onNodeWithText("Air Quality").assertDoesNotExist()
    }
}
