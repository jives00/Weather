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

class MediumWeatherWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val data = WidgetDataStore.getPrimary(context)
        provideContent {
            val bgTop = if (data != null) WidgetColors.gradientTopForCondition(data.condition, data.isDay) else Color(0xFF1565C0)
            GlanceTheme {
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(bgTop)
                        .padding(12.dp)
                        .clickable(actionStartActivity<MainActivity>())
                ) {
                    if (data == null) {
                        Text("—", style = TextStyle(color = ColorProvider(Color.White)))
                    } else {
                        // Top row: location + current temp
                        Row(
                            modifier = GlanceModifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = GlanceModifier.defaultWeight()) {
                                Text(
                                    text = data.locationName,
                                    style = TextStyle(color = ColorProvider(Color.White), fontSize = androidx.glance.unit.Sp(14f), fontWeight = FontWeight.Medium),
                                    maxLines = 1
                                )
                                Text(
                                    text = data.conditionDescription,
                                    style = TextStyle(color = ColorProvider(Color.White.copy(alpha = 0.8f)), fontSize = androidx.glance.unit.Sp(11f)),
                                    maxLines = 1
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${data.temperature}${data.temperatureSymbol}",
                                    style = TextStyle(color = ColorProvider(Color.White), fontSize = androidx.glance.unit.Sp(32f), fontWeight = FontWeight.Light)
                                )
                                Text(
                                    text = "H:${data.highTemp}° L:${data.lowTemp}°",
                                    style = TextStyle(color = ColorProvider(Color.White.copy(alpha = 0.75f)), fontSize = androidx.glance.unit.Sp(11f))
                                )
                            }
                        }

                        Spacer(GlanceModifier.height(8.dp))

                        // Hourly strip
                        if (data.hourlyTemps.isNotEmpty()) {
                            Row(
                                modifier = GlanceModifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                data.hourlyTemps.take(4).forEachIndexed { i, temp ->
                                    Column(
                                        modifier = GlanceModifier.defaultWeight(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "${i + 1}h",
                                            style = TextStyle(color = ColorProvider(Color.White.copy(alpha = 0.65f)), fontSize = androidx.glance.unit.Sp(10f))
                                        )
                                        Text(
                                            text = "$temp°",
                                            style = TextStyle(color = ColorProvider(Color.White), fontSize = androidx.glance.unit.Sp(13f))
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

class MediumWeatherWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = MediumWeatherWidget()
}
