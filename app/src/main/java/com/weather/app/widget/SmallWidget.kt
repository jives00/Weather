package com.weather.app.widget

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import android.appwidget.AppWidgetManager
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.weather.app.MainActivity
import com.weather.app.data.datastore.WidgetDataStore
import com.weather.app.work.WeatherRefreshWorker

class SmallWeatherWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val data = WidgetDataStore.getPrimary(context)
        provideContent {
            val bgTop = if (data != null) WidgetColors.gradientTopForCondition(data.condition, data.isDay) else Color(0xFF1565C0)
            Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(bgTop)
                        .clickable(actionStartActivity<MainActivity>()),
                    contentAlignment = Alignment.Center
                ) {
                    if (data == null) {
                        Text("—", style = TextStyle(color = ColorProvider(Color.White), fontSize = 22.sp))
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = WeatherDrawableMap.emojiFor(data.condition),
                                style = TextStyle(fontSize = 28.sp)
                            )
                            Text(
                                text = "${data.temperature}${data.temperatureSymbol}",
                                style = TextStyle(
                                    color = ColorProvider(Color.White),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
            }
        }
    }
}

class SmallWeatherWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = SmallWeatherWidget()

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        WorkManager.getInstance(context).enqueue(OneTimeWorkRequestBuilder<WeatherRefreshWorker>().build())
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }
}
