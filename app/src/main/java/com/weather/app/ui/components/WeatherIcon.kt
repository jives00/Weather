package com.weather.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.weather.app.domain.model.WeatherCondition

object WeatherIcon {
    fun forCondition(condition: WeatherCondition): ImageVector = when (condition) {
        WeatherCondition.CLEAR_DAY -> Icons.Filled.WbSunny
        WeatherCondition.CLEAR_NIGHT -> Icons.Filled.NightsStay
        WeatherCondition.PARTLY_CLOUDY_DAY -> Icons.Filled.WbCloudy
        WeatherCondition.PARTLY_CLOUDY_NIGHT -> Icons.Filled.Cloud
        WeatherCondition.OVERCAST -> Icons.Filled.Cloud
        WeatherCondition.FOG -> Icons.Filled.BlurOn
        WeatherCondition.DRIZZLE -> Icons.Filled.Grain
        WeatherCondition.RAIN -> Icons.Filled.WaterDrop
        WeatherCondition.FREEZING_RAIN -> Icons.Filled.AcUnit
        WeatherCondition.SNOW -> Icons.Filled.AcUnit
        WeatherCondition.RAIN_SHOWERS -> Icons.Filled.WaterDrop
        WeatherCondition.SNOW_SHOWERS -> Icons.Filled.AcUnit
        WeatherCondition.THUNDERSTORM -> Icons.Filled.Bolt
        WeatherCondition.UNKNOWN -> Icons.Filled.QuestionMark
    }

    fun colorForCondition(condition: WeatherCondition): Color = when (condition) {
        WeatherCondition.CLEAR_DAY -> Color(0xFFFFD54F)
        WeatherCondition.CLEAR_NIGHT -> Color(0xFFB0BEC5)
        WeatherCondition.PARTLY_CLOUDY_DAY -> Color(0xFFFFCA28)
        WeatherCondition.PARTLY_CLOUDY_NIGHT -> Color(0xFFB0BEC5)
        WeatherCondition.OVERCAST -> Color(0xFFCFD8DC)
        WeatherCondition.FOG -> Color(0xFFCFD8DC)
        WeatherCondition.DRIZZLE -> Color(0xFF81D4FA)
        WeatherCondition.RAIN -> Color(0xFF81D4FA)
        WeatherCondition.RAIN_SHOWERS -> Color(0xFF81D4FA)
        WeatherCondition.FREEZING_RAIN -> Color(0xFF80DEEA)
        WeatherCondition.SNOW -> Color(0xFFE1F5FE)
        WeatherCondition.SNOW_SHOWERS -> Color(0xFFE1F5FE)
        WeatherCondition.THUNDERSTORM -> Color(0xFFFFD54F)
        WeatherCondition.UNKNOWN -> Color.White
    }
}
