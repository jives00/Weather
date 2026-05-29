package com.weather.app.widget

import androidx.compose.ui.graphics.Color
import com.weather.app.domain.model.WeatherCondition

object WidgetColors {
    fun gradientTopForCondition(condition: WeatherCondition, isDay: Boolean): Color = when {
        condition == WeatherCondition.THUNDERSTORM -> Color(0xFF0D0D0D)
        condition == WeatherCondition.RAIN || condition == WeatherCondition.RAIN_SHOWERS || condition == WeatherCondition.DRIZZLE -> Color(0xFF1A2744)
        condition == WeatherCondition.SNOW || condition == WeatherCondition.SNOW_SHOWERS -> Color(0xFF455A64)
        condition == WeatherCondition.FOG -> Color(0xFF546E7A)
        condition == WeatherCondition.OVERCAST -> Color(0xFF263238)
        !isDay -> Color(0xFF0A0A1E)
        condition == WeatherCondition.PARTLY_CLOUDY_DAY -> Color(0xFF1976D2)
        else -> Color(0xFF1565C0)
    }

    fun gradientBottomForCondition(condition: WeatherCondition, isDay: Boolean): Color = when {
        condition == WeatherCondition.THUNDERSTORM -> Color(0xFF1C2233)
        condition == WeatherCondition.RAIN || condition == WeatherCondition.RAIN_SHOWERS || condition == WeatherCondition.DRIZZLE -> Color(0xFF37587A)
        condition == WeatherCondition.SNOW || condition == WeatherCondition.SNOW_SHOWERS -> Color(0xFFB0BEC5)
        condition == WeatherCondition.FOG -> Color(0xFF90A4AE)
        condition == WeatherCondition.OVERCAST -> Color(0xFF546E7A)
        !isDay -> Color(0xFF1A1A4E)
        condition == WeatherCondition.PARTLY_CLOUDY_DAY -> Color(0xFF64B5F6)
        else -> Color(0xFF42A5F5)
    }
}
