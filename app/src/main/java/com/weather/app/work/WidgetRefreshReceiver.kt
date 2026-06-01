package com.weather.app.work

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class WidgetRefreshReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        schedule(context)
        WorkManager.getInstance(context)
            .enqueue(OneTimeWorkRequestBuilder<WeatherRefreshWorker>().build())
    }

    companion object {
        private const val REQUEST_CODE = 1001

        fun schedule(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val triggerAt = System.currentTimeMillis() + AlarmManager.INTERVAL_HOUR
            val pi = pendingIntent(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
            } else {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
            }
        }

        private fun pendingIntent(context: Context) = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            Intent(context, WidgetRefreshReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
