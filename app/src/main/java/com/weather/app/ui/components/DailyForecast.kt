package com.weather.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.weather.app.domain.model.DailyWeather
import com.weather.app.domain.model.Units
import com.weather.app.ui.theme.OnWeatherSurface
import com.weather.app.ui.theme.OnWeatherSurfaceDim
import com.weather.app.ui.theme.WeatherDivider
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val dayFmt = DateTimeFormatter.ofPattern("EEE").withZone(ZoneId.systemDefault())
private val fullDayFmt = DateTimeFormatter.ofPattern("EEEE").withZone(ZoneId.systemDefault())

@Composable
fun DailyForecastList(
    daily: List<DailyWeather>,
    units: Units,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        daily.forEachIndexed { index, item ->
            DailyItem(item = item, isToday = index == 0, units = units)
            if (index < daily.lastIndex) {
                Divider(color = WeatherDivider, thickness = 0.5.dp)
            }
        }
    }
}

@Composable
private fun DailyItem(item: DailyWeather, isToday: Boolean, units: Units) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val instant = Instant.ofEpochSecond(item.time)
        Text(
            text = if (isToday) "Today" else dayFmt.format(instant),
            style = MaterialTheme.typography.bodyLarge,
            color = OnWeatherSurface,
            modifier = Modifier.width(68.dp)
        )

        if (item.precipitationProbability > 0) {
            Text(
                text = "${item.precipitationProbability}%",
                style = MaterialTheme.typography.labelMedium,
                color = OnWeatherSurfaceDim,
                modifier = Modifier.width(36.dp)
            )
        } else {
            Spacer(Modifier.width(36.dp))
        }

        Spacer(Modifier.weight(1f))

        Icon(
            imageVector = WeatherIcon.forCondition(item.condition),
            contentDescription = null,
            tint = OnWeatherSurface,
            modifier = Modifier.size(22.dp)
        )

        Spacer(Modifier.width(16.dp))

        Text(
            text = "${item.tempMin.toInt()}°",
            style = MaterialTheme.typography.bodyLarge,
            color = OnWeatherSurfaceDim,
            modifier = Modifier.width(40.dp)
        )

        Text(
            text = "${item.tempMax.toInt()}°",
            style = MaterialTheme.typography.bodyLarge,
            color = OnWeatherSurface,
            modifier = Modifier.width(40.dp)
        )
    }
}
