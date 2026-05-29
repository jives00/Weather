package com.weather.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val WeatherColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = OnPrimaryBlue,
    background = ClearNightTop,
    surface = ClearNightTop,
    onSurface = OnWeatherSurface,
    onBackground = OnWeatherSurface,
    surfaceVariant = WeatherCardBackground,
    onSurfaceVariant = OnWeatherSurface,
    outline = WeatherDivider
)

@Composable
fun WeatherTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = WeatherColorScheme,
        typography = WeatherTypography,
        content = content
    )
}
