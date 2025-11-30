package com.example.myapp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import com.google.gson.Gson

class LocationForegroundService : Service(), LocationListener {

    private val LOG_TAG = "FOREGROUND_LOC_SERVICE"
    private val CHANNEL_ID = "location_foreground_channel"
    private val SERVER_URL = "http://212.164.112.171:8000/push"

    private lateinit var locationManager: LocationManager
    private val gson = Gson()

    override fun onCreate() {
        super.onCreate()
        Log.d(LOG_TAG, "Service created")

        createNotificationChannel()

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Отслеживание местоположения")
            .setContentText("Сервис работает…")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(1, notification)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        requestLocationUpdates()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(LOG_TAG, "Service started")
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(LOG_TAG, "Service destroyed")
        try {
            locationManager.removeUpdates(this)
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Error removing updates", e)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(LOG_TAG, "Нет разрешений на геолокацию")
            return
        }

        try {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                1000L,   // интервал 5 секунд
                0.2f,      // мин. дистанция
                this
            )

            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000L,
                0f,
                this
            )

        } catch (e: Exception) {
            Log.e(LOG_TAG, "Ошибка запуска геолокации", e)
        }
    }

    override fun onLocationChanged(location: Location) {
        Log.d(LOG_TAG, "New location: ${location.latitude}, ${location.longitude}")

        sendLocationToServer(location)
    }

    private fun sendLocationToServer(location: Location) {
        val locationData = mapOf(
            "latitude" to location.latitude,
            "longitude" to location.longitude,
            "accuracy" to location.accuracy,
            "timestamp" to location.time,
            "recordedTime" to System.currentTimeMillis(),
            "source" to "service",
            "provider" to (location.provider ?: "N/A")
        )

        Thread {
            try {
                val json = gson.toJson(locationData)
                val client = OkHttpClient()
                val mediaType = "application/json".toMediaType()
                val body = json.toRequestBody(mediaType)

                val request = Request.Builder()
                    .url(SERVER_URL)
                    .post(body)
                    .build()

                val response = client.newCall(request).execute()
                Log.d(LOG_TAG, "Ответ сервера: ${response.body?.string()}")

            } catch (e: Exception) {
                Log.e(LOG_TAG, "Ошибка отправки: ${e.message}", e)
            }
        }.start()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Location Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
