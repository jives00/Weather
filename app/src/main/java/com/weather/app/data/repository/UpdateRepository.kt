package com.weather.app.data.repository

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import com.weather.app.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

data class ReleaseInfo(val tag: String, val apkUrl: String)

sealed class DownloadProgress {
    data class InProgress(val fraction: Float) : DownloadProgress()
    data class Complete(val uri: Uri?) : DownloadProgress()
    data object Failed : DownloadProgress()
}

class UpdateRepository {
    private val client = OkHttpClient()

    suspend fun checkForUpdate(): ReleaseInfo? = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("https://api.github.com/repos/jives00/Weather/releases/latest")
                .header("Accept", "application/vnd.github+json")
                .build()
            val body = client.newCall(request).execute().body?.string() ?: return@withContext null
            val json = JSONObject(body)
            val tag = json.getString("tag_name")
            if (tag == "v${BuildConfig.VERSION_NAME}") return@withContext null
            val assets = json.getJSONArray("assets")
            for (i in 0 until assets.length()) {
                val asset = assets.getJSONObject(i)
                if (asset.getString("name").endsWith(".apk")) {
                    return@withContext ReleaseInfo(tag, asset.getString("browser_download_url"))
                }
            }
            null
        } catch (_: Exception) { null }
    }

    fun downloadApk(context: Context, apkUrl: String, tag: String): Flow<DownloadProgress> = flow {
        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = dm.enqueue(
            DownloadManager.Request(Uri.parse(apkUrl))
                .setTitle("Weather $tag")
                .setDescription("Downloading update…")
                .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, "weather-update.apk")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
        )
        while (true) {
            val cursor = dm.query(DownloadManager.Query().setFilterById(downloadId))
            if (cursor.moveToFirst()) {
                when (cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))) {
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        cursor.close()
                        emit(DownloadProgress.Complete(dm.getUriForDownloadedFile(downloadId)))
                        break
                    }
                    DownloadManager.STATUS_FAILED -> {
                        cursor.close()
                        emit(DownloadProgress.Failed)
                        break
                    }
                    else -> {
                        val total = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                        val done = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                        cursor.close()
                        emit(DownloadProgress.InProgress(if (total > 0) done.toFloat() / total else 0f))
                    }
                }
            } else {
                cursor.close()
            }
            delay(300)
        }
    }
}
