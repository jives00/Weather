package com.weather.app

import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit4.runners.AndroidJUnit4
import com.weather.app.domain.model.Units
import com.weather.app.ui.theme.WeatherTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun settings_imperialSelectedByDefault() {
        var selected by mutableStateOf(Units.IMPERIAL)
        composeRule.setContent {
            WeatherTheme {
                com.weather.app.ui.settings.SettingsScreenPreview(
                    units = selected,
                    onUnitsChange = { selected = it }
                )
            }
        }
        composeRule.onNodeWithText(Units.IMPERIAL.label).assertIsDisplayed()
        composeRule.onNodeWithText(Units.METRIC.label).assertIsDisplayed()
    }

    @Test
    fun settings_switchToMetric() {
        var selected by mutableStateOf(Units.IMPERIAL)
        composeRule.setContent {
            WeatherTheme {
                com.weather.app.ui.settings.SettingsScreenPreview(
                    units = selected,
                    onUnitsChange = { selected = it }
                )
            }
        }
        composeRule.onNodeWithText(Units.METRIC.label).performClick()
        assert(selected == Units.METRIC)
    }
}
