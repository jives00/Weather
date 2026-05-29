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

class BarWeatherWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val data = WidgetDataStore.getPrimary(context)
        provideContent {
            val bgTop = if (data != null) WidgetColors.gradientTopForCondition(data.condition, data.isDay) else Color(0xFF1565C0)
            GlanceTheme {
                Row(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(bgTop)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable(actionStartActivity<MainActivity>()),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (data == null) {
                        Text("—", style = TextStyle(color = ColorProvider(Color.White)))
                    } else {
                        Text(
                            text = data.locationName,
                            style = TextStyle(color = ColorProvider(Color.White.copy(alpha = 0.85f)), fontSize = androidx.glance.unit.Sp(13f)),
                            modifier = GlanceModifier.defaultWeight(),
                            maxLines = 1
                        )
                        Spacer(GlanceModifier.width(8.dp))
                        Text(
                            text = "${data.temperature}${data.temperatureSymbol}",
                            style = TextStyle(color = ColorProvider(Color.White), fontSize = androidx.glance.unit.Sp(22f), fontWeight = FontWeight.Medium)
                        )
                        Spacer(GlanceModifier.width(8.dp))
                        Text(
                            text = data.conditionDescription,
                            style = TextStyle(color = ColorProvider(Color.White.copy(alpha = 0.8f)), fontSize = androidx.glance.unit.Sp(12f)),
                            modifier = GlanceModifier.defaultWeight(),
                            maxLines = 1
                        )
                        Spacer(GlanceModifier.width(8.dp))
                        Text(
                            text = "H:${data.highTemp}° L:${data.lowTemp}°",
                            style = TextStyle(color = ColorProvider(Color.White.copy(alpha = 0.75f)), fontSize = androidx.glance.unit.Sp(11f))
                        )
                    }
                }
            }
        }
    }
}

class BarWeatherWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = BarWeatherWidget()
}
