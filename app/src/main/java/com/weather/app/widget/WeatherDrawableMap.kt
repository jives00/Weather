package com.weather.app.widget

import com.weather.app.R
import com.weather.app.domain.model.WeatherCondition

object WeatherDrawableMap {
    fun drawableFor(condition: WeatherCondition): Int = when (condition) {
        WeatherCondition.CLEAR_DAY -> R.drawable.ic_weather_clear_day
        WeatherCondition.CLEAR_NIGHT -> R.drawable.ic_weather_clear_night
        WeatherCondition.PARTLY_CLOUDY_DAY, WeatherCondition.PARTLY_CLOUDY_NIGHT -> R.drawable.ic_weather_partly_cloudy
        WeatherCondition.OVERCAST, WeatherCondition.FOG -> R.drawable.ic_weather_cloudy
        WeatherCondition.DRIZZLE, WeatherCondition.RAIN, WeatherCondition.FREEZING_RAIN,
        WeatherCondition.RAIN_SHOWERS -> R.drawable.ic_weather_rain
        WeatherCondition.SNOW, WeatherCondition.SNOW_SHOWERS -> R.drawable.ic_weather_snow
        WeatherCondition.THUNDERSTORM -> R.drawable.ic_weather_storm
        WeatherCondition.UNKNOWN -> R.drawable.ic_weather_cloudy
    }

    fun emojiFor(condition: WeatherCondition): String = when (condition) {
        WeatherCondition.CLEAR_DAY -> "☀️"
        WeatherCondition.CLEAR_NIGHT -> "🌙"
        WeatherCondition.PARTLY_CLOUDY_DAY -> "⛅"
        WeatherCondition.PARTLY_CLOUDY_NIGHT -> "🌙"
        WeatherCondition.OVERCAST -> "☁️"
        WeatherCondition.FOG -> "🌫️"
        WeatherCondition.DRIZZLE -> "🌦️"
        WeatherCondition.RAIN, WeatherCondition.RAIN_SHOWERS -> "🌧️"
        WeatherCondition.FREEZING_RAIN -> "🌨️"
        WeatherCondition.SNOW, WeatherCondition.SNOW_SHOWERS -> "❄️"
        WeatherCondition.THUNDERSTORM -> "⛈️"
        WeatherCondition.UNKNOWN -> "🌤️"
    }
}
