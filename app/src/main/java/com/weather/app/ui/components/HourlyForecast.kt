package com.weather.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.weather.app.domain.model.HourlyWeather
import com.weather.app.domain.model.Units
import com.weather.app.ui.theme.OnWeatherSurface
import com.weather.app.ui.theme.OnWeatherSurfaceDim
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val timeFmt = DateTimeFormatter.ofPattern("ha").withZone(ZoneId.systemDefault())

@Composable
fun HourlyForecastRow(
    hourly: List<HourlyWeather>,
    units: Units,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(hourly) { item ->
            HourlyItem(item = item, units = units)
        }
    }
}

@Composable
private fun HourlyItem(item: HourlyWeather, units: Units) {
    Column(
        modifier = Modifier
            .width(56.dp)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = timeFmt.format(Instant.ofEpochSecond(item.time)).lowercase(),
            style = MaterialTheme.typography.labelMedium,
            color = OnWeatherSurfaceDim
        )
        Icon(
            imageVector = WeatherIcon.forCondition(item.condition),
            contentDescription = null,
            tint = OnWeatherSurface,
            modifier = Modifier.size(20.dp)
        )
        if (item.precipitationProbability > 0) {
            Text(
                text = "${item.precipitationProbability}%",
                style = MaterialTheme.typography.labelSmall,
                color = OnWeatherSurfaceDim
            )
        } else {
            Spacer(Modifier.height(14.dp))
        }
        Text(
            text = "${item.temperature.toInt()}${units.temperatureSymbol}",
            style = MaterialTheme.typography.labelLarge,
            color = OnWeatherSurface
        )
    }
}
