package com.weather.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.weather.app.domain.model.DailyWeather
import com.weather.app.domain.model.HourlyWeather
import com.weather.app.domain.model.Units
import com.weather.app.ui.theme.OnWeatherSurface
import com.weather.app.ui.theme.OnWeatherSurfaceDim
import com.weather.app.ui.theme.WeatherCardBackground
import com.weather.app.ui.theme.WeatherDivider
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val dayFmt = DateTimeFormatter.ofPattern("EEE").withZone(ZoneId.systemDefault())
private val hourFmt = DateTimeFormatter.ofPattern("h a").withZone(ZoneId.systemDefault())
private val zone = ZoneId.systemDefault()

@Composable
fun DailyForecastSection(
    daily: List<DailyWeather>,
    hourly: List<HourlyWeather>,
    units: Units,
    modifier: Modifier = Modifier
) {
    var expandedIndex by remember { mutableIntStateOf(-1) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "10-day forecast",
            style = MaterialTheme.typography.titleMedium,
            color = OnWeatherSurface,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(WeatherCardBackground)
        ) {
            daily.forEachIndexed { index, day ->
                val isExpanded = expandedIndex == index
                DailyRow(
                    day = day,
                    isToday = index == 0,
                    isExpanded = isExpanded,
                    onClick = { expandedIndex = if (isExpanded) -1 else index }
                )
                AnimatedVisibility(visible = isExpanded) {
                    DayHourlyDetail(day = day, hourly = hourly, units = units)
                }
                if (index < daily.lastIndex) {
                    Divider(color = WeatherDivider, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
}

@Composable
private fun DailyRow(
    day: DailyWeather,
    isToday: Boolean,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (isToday) "Today" else dayFmt.format(Instant.ofEpochSecond(day.time)),
            style = MaterialTheme.typography.bodyLarge,
            color = OnWeatherSurface,
            modifier = Modifier.width(64.dp)
        )

        if (day.precipitationProbability > 0) {
            Text(
                text = "${day.precipitationProbability}%",
                style = MaterialTheme.typography.labelMedium,
                color = androidx.compose.ui.graphics.Color(0xFF81D4FA),
                modifier = Modifier.width(36.dp)
            )
        } else {
            Spacer(Modifier.width(36.dp))
        }

        Spacer(Modifier.weight(1f))

        Icon(
            imageVector = WeatherIcon.forCondition(day.condition),
            contentDescription = null,
            tint = WeatherIcon.colorForCondition(day.condition),
            modifier = Modifier.size(22.dp)
        )

        Spacer(Modifier.width(16.dp))

        Text(
            text = "${day.tempMin.toInt()}°",
            style = MaterialTheme.typography.bodyLarge,
            color = OnWeatherSurfaceDim,
            modifier = Modifier.width(36.dp)
        )
        Text(
            text = "${day.tempMax.toInt()}°",
            style = MaterialTheme.typography.bodyLarge,
            color = OnWeatherSurface,
            modifier = Modifier.width(36.dp)
        )
    }
}

@Composable
private fun DayHourlyDetail(
    day: DailyWeather,
    hourly: List<HourlyWeather>,
    units: Units
) {
    val dayDate = remember(day.time) {
        Instant.ofEpochSecond(day.time).atZone(zone).toLocalDate()
    }
    val dayHours = remember(day.time, hourly) {
        hourly.filter {
            Instant.ofEpochSecond(it.time).atZone(zone).toLocalDate() == dayDate
        }
    }

    if (dayHours.isEmpty()) return

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(androidx.compose.ui.graphics.Color(0x20000000))
            .padding(vertical = 12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(dayHours) { hour ->
            Column(
                modifier = Modifier.width(52.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = "${hour.temperature.toInt()}°",
                    style = MaterialTheme.typography.labelLarge,
                    color = OnWeatherSurface
                )
                if (hour.precipitationProbability > 0) {
                    Text(
                        text = "${hour.precipitationProbability}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = androidx.compose.ui.graphics.Color(0xFF81D4FA)
                    )
                } else {
                    Spacer(Modifier.height(14.dp))
                }
                Icon(
                    imageVector = WeatherIcon.forCondition(hour.condition),
                    contentDescription = null,
                    tint = WeatherIcon.colorForCondition(hour.condition),
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = hourFmt.format(Instant.ofEpochSecond(hour.time)).lowercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = OnWeatherSurfaceDim
                )
            }
        }
    }
}
