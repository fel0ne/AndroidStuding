package com.example.myapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.location.LocationListener
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

import java.util.concurrent.TimeUnit

class LocServActivity : LocationListener, AppCompatActivity()  {

    val LOG_TAG: String = "LOCATION_ACTIVITY"

    private val PERMISSION_REQUEST_ACCESS_LOCATION = 100


    private val SERVER_URL = "http://212.164.112.171:8000/push"

    private lateinit var locationManager: LocationManager
    private lateinit var tvLat: TextView
    private lateinit var tvLon: TextView
    lateinit var upBut: Button
    private lateinit var tvAppContext: TextView
    private lateinit var tvActivityContext: TextView



    private val gson: Gson = GsonBuilder().create()
    private var lastLocation: Location? = null


    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_loc_serv)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        tvLat = findViewById(R.id.tv_lat) as TextView
        tvLon = findViewById(R.id.tv_lon) as TextView
        tvAppContext = findViewById(R.id.tv_appContext) as TextView
        tvActivityContext = findViewById(R.id.tv_activityContext) as TextView
        upBut = findViewById(R.id.buttonUpdate) as Button

        tvAppContext.setText(applicationContext.toString())
        tvActivityContext.setText(this.toString())



        requestLocationUpdates()


        upBut.setOnClickListener {
            lastLocation?.let { location ->
                sendLocationToServer(location, "manual")
            } ?: run {
                Toast.makeText(this, "Нет данных о местоположении", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun stopLocationUpdates() {
        try {
            locationManager.removeUpdates(this)
        } catch (e: SecurityException) {
            Log.e(LOG_TAG, "SecurityException при остановке обновлений", e)
        }
    }

    private fun requestLocationUpdates(){
        if(checkPermissions() && isLocationEnabled()){
            try {
                if (ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }


//                locationManager.requestLocationUpdates(
//                    LocationManager.GPS_PROVIDER,
//                    1000L,  // 5 секунд
//                    0f,    // 10 метров
//                    this
//                )
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    1000L,
                    0.2f,
                    this
                )

            } catch (e: SecurityException) {
                Log.e(LOG_TAG, "Ошибка безопасности при запросе обновлений", e)
            }
        } else if (!checkPermissions()) {
            requestPermissions()
        } else if (!isLocationEnabled()) {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }

    private fun updateLocationUI(location: Location, source: String) {
        tvLat.setText("Широта: ${location.latitude}")
        tvLon.setText("Долгота: ${location.longitude} (Источник: $source)")



        sendLocationToServer(location, source)
    }

    override fun onLocationChanged(location: Location) {
        lastLocation = location
        updateLocationUI(location, "Live ${location.provider}")
    }


    private fun sendLocationToServer(location: Location, source: String) {
        val locationData = LocationData(
            latitude = location.latitude,
            longitude = location.longitude,
            accuracy = location.accuracy,
            timestamp = location.time,
            recordedTime = System.currentTimeMillis(),
            source = source,
            provider = location.provider ?: "N/A"
        )

        Thread {
            try {
                val mediaType = "application/json; charset=utf-8".toMediaType()
                val jsonString = gson.toJson(locationData)
                val body = jsonString.toRequestBody(mediaType)

                Log.d(LOG_TAG, "Отправка данных: $jsonString")
                Log.d(LOG_TAG, "URL: $SERVER_URL")

                val request = Request.Builder()
                    .url(SERVER_URL)
                    .post(body)
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                runOnUiThread {
                    if (response.isSuccessful) {
                        Log.d(LOG_TAG, " УСПЕХ: Данные отправлены на сервер: $responseBody")
                        Toast.makeText(this, "Данные отправлены!", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e(LOG_TAG, " Ошибка сервера: ${response.code} - $responseBody")
                        Toast.makeText(this, "Ошибка сервера: ${response.code}", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: Exception) {
                Log.e(LOG_TAG, "Ошибка отправки на сервер: ${e.message}", e)
                runOnUiThread {
                    Toast.makeText(this, "Ошибка сети: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }


    data class LocationData(
        val latitude: Double,
        val longitude: Double,
        val accuracy: Float,
        val timestamp: Long,
        val recordedTime: Long,
        val source: String,
        val provider: String
    )

    private fun requestPermissions() {
        Log.w(LOG_TAG, "requestPermissions()")
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }
    override fun onResume() {
        super.onResume()
        stopService(Intent(this, LocationForegroundService::class.java))
    }

    override fun onPause() {
        super.onPause()
        startService(Intent(this, LocationForegroundService::class.java))
    }


    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, LocationForegroundService::class.java))
    }
    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_ACCESS_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "Разрешение предоставлено", Toast.LENGTH_SHORT).show()
                requestLocationUpdates()
            } else {
                Toast.makeText(applicationContext, "Отказано пользователем", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun isLocationEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}