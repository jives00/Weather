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

class SmallWeatherWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val data = WidgetDataStore.getPrimary(context)
        provideContent {
            val bgTop = if (data != null) WidgetColors.gradientTopForCondition(data.condition, data.isDay) else Color(0xFF1565C0)
            GlanceTheme {
                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(bgTop)
                        .clickable(actionStartActivity<MainActivity>()),
                    contentAlignment = Alignment.Center
                ) {
                    if (data == null) {
                        Text("—", style = TextStyle(color = ColorProvider(Color.White), fontSize = androidx.glance.unit.Sp(22f)))
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${data.temperature}${data.temperatureSymbol}",
                                style = TextStyle(
                                    color = ColorProvider(Color.White),
                                    fontSize = androidx.glance.unit.Sp(28f),
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Text(
                                text = data.conditionDescription,
                                style = TextStyle(
                                    color = ColorProvider(Color.White.copy(alpha = 0.8f)),
                                    fontSize = androidx.glance.unit.Sp(11f)
                                ),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

class SmallWeatherWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = SmallWeatherWidget()
}
