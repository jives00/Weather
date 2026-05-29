package com.weather.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.weather.app.domain.model.CurrentWeather
import com.weather.app.domain.model.DailyWeather
import com.weather.app.domain.model.Units
import com.weather.app.domain.model.WeatherForecast
import com.weather.app.ui.theme.OnWeatherSurface
import com.weather.app.ui.theme.OnWeatherSurfaceDim
import com.weather.app.ui.theme.WeatherCardBackground
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val timeFmt = DateTimeFormatter.ofPattern("h:mm a").withZone(ZoneId.systemDefault())

@Composable
fun DetailCardsGrid(
    forecast: WeatherForecast,
    modifier: Modifier = Modifier
) {
    val current = forecast.current
    val today = forecast.daily.firstOrNull()

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DetailCard(
                icon = Icons.Filled.Water,
                label = "Humidity",
                value = "${current.humidity}%",
                modifier = Modifier.weight(1f)
            )
            DetailCard(
                icon = Icons.Filled.Air,
                label = "Wind",
                value = "${current.windSpeed.toInt()} ${forecast.units.windSpeedUnit}",
                sub = windDirectionLabel(current.windDirection),
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DetailCard(
                icon = Icons.Filled.WbSunny,
                label = "UV Index",
                value = current.uvIndex.toInt().toString(),
                sub = uvLabel(current.uvIndex),
                modifier = Modifier.weight(1f)
            )
            DetailCard(
                icon = Icons.Filled.Visibility,
                label = "Visibility",
                value = "${(current.visibility / 1000).toInt()} ${forecast.units.visibilityUnit}",
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DetailCard(
                icon = Icons.Filled.Compress,
                label = "Pressure",
                value = "${current.pressure.toInt()} ${forecast.units.pressureUnit}",
                modifier = Modifier.weight(1f)
            )
            DetailCard(
                icon = Icons.Filled.Thermostat,
                label = "Feels Like",
                value = "${current.feelsLike.toInt()}${forecast.units.temperatureSymbol}",
                modifier = Modifier.weight(1f)
            )
        }
        if (today != null) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DetailCard(
                    icon = Icons.Filled.WbTwilight,
                    label = "Sunrise",
                    value = timeFmt.format(Instant.ofEpochSecond(today.sunrise)),
                    modifier = Modifier.weight(1f)
                )
                DetailCard(
                    icon = Icons.Filled.Nightlight,
                    label = "Sunset",
                    value = timeFmt.format(Instant.ofEpochSecond(today.sunset)),
                    modifier = Modifier.weight(1f)
                )
            }
        }
        forecast.aqi?.let { aqi ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DetailCard(
                    icon = Icons.Filled.Air,
                    label = "Air Quality",
                    value = aqi.toString(),
                    sub = aqiLabel(aqi),
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun DetailCard(
    icon: ImageVector,
    label: String,
    value: String,
    sub: String? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(WeatherCardBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = OnWeatherSurfaceDim, modifier = Modifier.size(16.dp))
            Text(text = label.uppercase(), style = MaterialTheme.typography.labelSmall, color = OnWeatherSurfaceDim)
        }
        Text(text = value, style = MaterialTheme.typography.titleLarge, color = OnWeatherSurface)
        if (sub != null) {
            Text(text = sub, style = MaterialTheme.typography.bodyMedium, color = OnWeatherSurfaceDim)
        }
    }
}

private fun windDirectionLabel(degrees: Int): String {
    val dirs = listOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
    return dirs[((degrees + 22.5) / 45).toInt() % 8]
}

private fun uvLabel(uv: Double): String = when {
    uv < 3 -> "Low"
    uv < 6 -> "Moderate"
    uv < 8 -> "High"
    uv < 11 -> "Very High"
    else -> "Extreme"
}

private fun aqiLabel(aqi: Int): String = when {
    aqi <= 50 -> "Good"
    aqi <= 100 -> "Moderate"
    aqi <= 150 -> "Unhealthy for Sensitive"
    aqi <= 200 -> "Unhealthy"
    aqi <= 300 -> "Very Unhealthy"
    else -> "Hazardous"
}
