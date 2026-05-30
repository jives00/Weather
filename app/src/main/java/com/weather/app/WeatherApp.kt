package com.weather.app

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.weather.app.work.WeatherRefreshWorker
import com.weather.app.work.WidgetRefreshReceiver
import java.util.concurrent.TimeUnit

class WeatherApp : Application() {
    override fun onCreate() {
        super.onCreate()
        scheduleWeatherRefresh()
        WidgetRefreshReceiver.schedule(this)
    }

    private fun scheduleWeatherRefresh() {
        val request = PeriodicWorkRequestBuilder<WeatherRefreshWorker>(60, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            WeatherRefreshWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}
