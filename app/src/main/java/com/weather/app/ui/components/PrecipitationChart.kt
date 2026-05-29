package com.weather.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.weather.app.domain.model.HourlyWeather
import com.weather.app.domain.model.Units
import com.weather.app.domain.model.WeatherCondition
import com.weather.app.ui.theme.OnWeatherSurface
import com.weather.app.ui.theme.OnWeatherSurfaceDim
import com.weather.app.ui.theme.WeatherCardBackground
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val timeFmt = DateTimeFormatter.ofPattern("h:mm a").withZone(ZoneId.systemDefault())
private val hourFmt = DateTimeFormatter.ofPattern("h a").withZone(ZoneId.systemDefault())

@Composable
fun PrecipitationForecastCard(
    hourly: List<HourlyWeather>,
    units: Units,
    modifier: Modifier = Modifier
) {
    val nowEpoch = System.currentTimeMillis() / 1000
    val nextHours = remember(hourly) { hourly.filter { it.time >= nowEpoch }.take(12) }

    val precipHours = nextHours.filter { it.precipitation > 0.05 || it.precipitationProbability > 25 }
    if (precipHours.isEmpty()) return

    val isSnow = precipHours.first().condition in listOf(WeatherCondition.SNOW, WeatherCondition.SNOW_SHOWERS)
    val precipType = if (isSnow) "Snow" else "Rain"

    val currentlyPrecip = nextHours.firstOrNull()
        ?.let { it.precipitation > 0.1 || it.precipitationProbability > 50 } == true

    val headerText: String
    val subText: String
    if (currentlyPrecip) {
        val endHour = precipHours.last()
        headerText = "$precipType continuing through ${timeFmt.format(Instant.ofEpochSecond(endHour.time))}"
        subText = "Tapering off later this hour"
    } else {
        val startHour = precipHours.first()
        val endHour = precipHours.last()
        val startStr = timeFmt.format(Instant.ofEpochSecond(startHour.time))
        val endStr = timeFmt.format(Instant.ofEpochSecond(endHour.time))
        headerText = "$precipType expected around $startStr"
        subText = "Continuing through $endStr"
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(WeatherCardBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(headerText, style = MaterialTheme.typography.titleMedium, color = OnWeatherSurface)
        Text(subText, style = MaterialTheme.typography.bodyMedium, color = OnWeatherSurfaceDim)
        Spacer(Modifier.height(8.dp))
        PrecipBarChart(hours = nextHours.take(8), precipType = precipType)
    }
}

@Composable
private fun PrecipBarChart(hours: List<HourlyWeather>, precipType: String) {
    val barColor = if (precipType == "Snow") Color(0xFFB0BEC5) else Color(0xFF4FC3F7)
    val maxVal = hours.maxOfOrNull { maxOf(it.precipitation, it.precipitationProbability / 100.0) }?.coerceAtLeast(0.1) ?: 1.0

    Box(modifier = Modifier.fillMaxWidth().height(72.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val chartBottom = h * 0.75f
            val chartHeight = chartBottom * 0.9f
            val labelAreaW = 48f
            val chartW = w - labelAreaW
            val barW = chartW / hours.size

            // Y-axis labels
            val labels = listOf("Heavy" to 0.0f, "Mod" to 0.33f, "Light" to 0.66f)
            labels.forEach { (label, frac) ->
                val y = chartBottom - chartHeight * (1f - frac)
                drawLine(Color(0x20FFFFFF), Offset(labelAreaW, y), Offset(w, y), strokeWidth = 0.5f)
            }

            // Baseline
            drawLine(Color(0x50FFFFFF), Offset(labelAreaW, chartBottom), Offset(w, chartBottom), strokeWidth = 1f)

            // Bars
            hours.forEachIndexed { i, hour ->
                val value = maxOf(hour.precipitation, hour.precipitationProbability / 100.0)
                val normalized = (value / maxVal).toFloat().coerceIn(0f, 1f)
                val barH = normalized * chartHeight
                if (barH > 1f) {
                    val left = labelAreaW + i * barW + barW * 0.15f
                    val top = chartBottom - barH
                    drawRoundRect(
                        color = barColor.copy(alpha = 0.8f),
                        topLeft = Offset(left, top),
                        size = Size(barW * 0.7f, barH),
                        cornerRadius = CornerRadius(4f)
                    )
                }
            }
        }
        // Time labels row — drawn separately for text support
        Row(
            modifier = Modifier.fillMaxWidth().align(androidx.compose.ui.Alignment.BottomStart).padding(start = 48.dp),
        ) {
            hours.forEachIndexed { i, hour ->
                if (i % 2 == 0) {
                    Box(modifier = Modifier.weight(2f)) {
                        Text(
                            text = hourFmt.format(Instant.ofEpochSecond(hour.time)).lowercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = OnWeatherSurfaceDim
                        )
                    }
                } else {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}
