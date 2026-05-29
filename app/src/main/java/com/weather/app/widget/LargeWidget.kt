package com.weather.app.widget

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.glance.*
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.weather.app.MainActivity
import com.weather.app.data.datastore.WidgetDataStore

class LargeWeatherWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val data = WidgetDataStore.getPrimary(context)
        provideContent {
            val bgTop = if (data != null) WidgetColors.gradientTopForCondition(data.condition, data.isDay) else Color(0xFF1565C0)
            GlanceTheme {
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(bgTop)
                        .padding(16.dp)
                        .clickable(actionStartActivity<MainActivity>())
                ) {
                    if (data == null) {
                        Box(modifier = GlanceModifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("—", style = TextStyle(color = ColorProvider(Color.White)))
                        }
                    } else {
                        // Location name
                        Text(
                            text = data.locationName,
                            style = TextStyle(color = ColorProvider(Color.White.copy(alpha = 0.85f)), fontSize = androidx.glance.unit.Sp(14f)),
                            maxLines = 1
                        )

                        Spacer(GlanceModifier.height(4.dp))

                        // Temperature + condition row
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${data.temperature}${data.temperatureSymbol}",
                                style = TextStyle(color = ColorProvider(Color.White), fontSize = androidx.glance.unit.Sp(56f), fontWeight = FontWeight.Light)
                            )
                            Spacer(GlanceModifier.width(12.dp))
                            Column {
                                Text(
                                    text = data.conditionDescription,
                                    style = TextStyle(color = ColorProvider(Color.White.copy(alpha = 0.85f)), fontSize = androidx.glance.unit.Sp(13f))
                                )
                                Text(
                                    text = "H:${data.highTemp}° L:${data.lowTemp}°",
                                    style = TextStyle(color = ColorProvider(Color.White.copy(alpha = 0.7f)), fontSize = androidx.glance.unit.Sp(12f))
                                )
                                Text(
                                    text = "Feels ${data.feelsLike}${data.temperatureSymbol}",
                                    style = TextStyle(color = ColorProvider(Color.White.copy(alpha = 0.7f)), fontSize = androidx.glance.unit.Sp(12f))
                                )
                            }
                        }

                        Spacer(GlanceModifier.height(12.dp))

                        // Hourly strip
                        if (data.hourlyTemps.isNotEmpty()) {
                            Row(modifier = GlanceModifier.fillMaxWidth()) {
                                data.hourlyTemps.take(4).forEachIndexed { i, temp ->
                                    Column(
                                        modifier = GlanceModifier.defaultWeight().padding(4.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "${i + 1}h",
                                            style = TextStyle(color = ColorProvider(Color.White.copy(alpha = 0.6f)), fontSize = androidx.glance.unit.Sp(10f))
                                        )
                                        val cond = data.hourlyConditions.getOrNull(i)?.let {
                                            runCatching { com.weather.app.domain.model.WeatherCondition.valueOf(it) }.getOrNull()
                                        }
                                        Text(
                                            text = "$temp°",
                                            style = TextStyle(color = ColorProvider(Color.White), fontSize = androidx.glance.unit.Sp(14f))
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

class LargeWeatherWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = LargeWeatherWidget()
}
