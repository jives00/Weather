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

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Details",
            style = MaterialTheme.typography.titleMedium,
            color = OnWeatherSurface,
            modifier = Modifier.padding(horizontal = 0.dp)
        )

        CardRow {
            DetailCard(Icons.Filled.Water, "Humidity", "${current.humidity}%", sub = "Relative", modifier = Modifier.weight(1f))
            DetailCard(Icons.Filled.Air, "Wind", "${current.windSpeed.toInt()} ${forecast.units.windSpeedUnit}", sub = windDirectionLabel(current.windDirection), modifier = Modifier.weight(1f))
        }
        CardRow {
            DetailCard(Icons.Filled.WbSunny, "UV Index", current.uvIndex.toInt().toString(), sub = uvLabel(current.uvIndex), modifier = Modifier.weight(1f))
            DetailCard(Icons.Filled.Visibility, "Visibility", "${(current.visibility / 1000).toInt()} ${forecast.units.visibilityUnit}", sub = forecast.units.visibilityUnit, modifier = Modifier.weight(1f))
        }
        CardRow {
            DetailCard(Icons.Filled.Compress, "Pressure", "${current.pressure.toInt()}", sub = forecast.units.pressureUnit, modifier = Modifier.weight(1f))
            if (forecast.aqi != null) {
                DetailCard(Icons.Filled.Air, "Air Quality", forecast.aqi.toString(), sub = aqiLabel(forecast.aqi), modifier = Modifier.weight(1f))
            } else {
                Spacer(Modifier.weight(1f))
            }
        }
        if (today != null) {
            CardRow {
                DetailCard(Icons.Filled.WbTwilight, "Sunrise", timeFmt.format(Instant.ofEpochSecond(today.sunrise)), sub = "Today", modifier = Modifier.weight(1f))
                DetailCard(Icons.Filled.Nightlight, "Sunset", timeFmt.format(Instant.ofEpochSecond(today.sunset)), sub = "Today", modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun CardRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        content = content
    )
}

@Composable
private fun DetailCard(
    icon: ImageVector,
    label: String,
    value: String,
    sub: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(16.dp))
            .background(WeatherCardBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = OnWeatherSurfaceDim, modifier = Modifier.size(14.dp))
            Text(text = label.uppercase(), style = MaterialTheme.typography.labelSmall, color = OnWeatherSurfaceDim)
        }
        Text(text = value, style = MaterialTheme.typography.titleLarge, color = OnWeatherSurface)
        Text(text = sub, style = MaterialTheme.typography.bodyMedium, color = OnWeatherSurfaceDim)
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
