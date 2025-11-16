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
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.Locale

class LocationActivity : LocationListener, AppCompatActivity()  {

    val LOG_TAG: String = "LOCATION_ACTIVITY"


    private val PERMISSION_REQUEST_ACCESS_LOCATION= 100
    private  val LOCATION_FILE_NAME = "location_history.json"

    private lateinit var locationManager: LocationManager
    private lateinit var tvLat: TextView
    private lateinit var tvLon: TextView
    lateinit var upBut: Button
    private lateinit var tvAppContext: TextView
    private lateinit var tvActivityContext: TextView

    private lateinit var storageDir: File

    private val gson: Gson = GsonBuilder().create()
    private var lastLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_location)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Инициализация UI и сервисов
        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        tvLat = findViewById(R.id.tv_lat) as TextView
        tvLon = findViewById(R.id.tv_lon) as TextView
        tvAppContext = findViewById(R.id.tv_appContext) as TextView
        tvActivityContext = findViewById(R.id.tv_activityContext) as TextView
        upBut = findViewById(R.id.buttonUpdate) as Button

        tvAppContext.setText(applicationContext.toString())
        tvActivityContext.setText(this.toString())

        storageDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOCUMENTS)


        upBut.setOnClickListener {

            getLastKnownLocation()
        }
    }

    override fun onResume() {
        super.onResume()

        if (checkPermissions() && isLocationEnabled()) {
            getLastKnownLocation()
        }
    }

    override fun onPause() {
        super.onPause()

        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        try {
            locationManager.removeUpdates(this)
        } catch (e: SecurityException) {
            Log.e(LOG_TAG, "SecurityException при остановке обновлений", e)
        }
    }


    private fun getLastKnownLocation() {
        if (!checkPermissions()) {
            requestPermissions()
            return
        }
        if (!isLocationEnabled()) {
            Toast.makeText(this, "Включите службы местоположения", Toast.LENGTH_SHORT).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
            return
        }

        try {

            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }


            val providers = locationManager.getProviders(true)
            var bestLocation: Location? = null
            for (provider in providers) {
                val location = locationManager.getLastKnownLocation(provider)
                if (location != null && (bestLocation == null || location.accuracy < bestLocation.accuracy)) {
                    bestLocation = location
                }
            }

            if (bestLocation != null) {
                lastLocation = bestLocation
                updateLocationUI(bestLocation, "Кэш")
                Log.d(LOG_TAG, "Получено последнее известное местоположение (Кэш)")
                stopLocationUpdates()
            } else {
                Toast.makeText(this, "Кэш пуст. Запускаю активный поиск...", Toast.LENGTH_SHORT).show()

                requestLocationUpdates()
            }

        } catch (e: Exception) {
            Log.e(LOG_TAG, "Ошибка при получении последнего известного местоположения", e)
            Toast.makeText(this, "Ошибка получения местоположения", Toast.LENGTH_SHORT).show()
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

                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000L,
                    1f,
                    this
                )
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    1000L,
                    1f,
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
        saveLocationToJson(location, source) // Сохранение в файл
    }


    override fun onLocationChanged(location: Location) {
        lastLocation = location
        // Если пришло новое, более точное местоположение
        updateLocationUI(location, "Live ${location.provider}")
    }


    private fun saveLocationToJson(location: Location, source: String) {
        try {
            val locationData = LocationData(
                latitude = location.latitude,
                longitude = location.longitude,
                accuracy = location.accuracy,
                timestamp = location.time,
                recordedTime = System.currentTimeMillis(),
                source = source,
                provider = location.provider ?: "N/A"
            )

            val jsonString = gson.toJson(locationData)
            val file = File(storageDir, LOCATION_FILE_NAME)


            FileOutputStream(file, true).use { fos ->
                PrintWriter(fos).apply {

                    if (file.length() == 0L) {
                        print("[")
                    } else if (file.length() > 1 && file.absoluteFile.readText().endsWith("]")) {
                        println(",")
                    } else if (file.length() > 0) {
                        println(",")
                    }

                    print(jsonString)
                    flush()
                }
            }


            Log.d(LOG_TAG, "Местоположение сохранено в JSON: ${file.absolutePath}")

        } catch (e: Exception) {
            Log.e(LOG_TAG, "Ошибка сохранения местоположения в JSON", e)

        }
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
        Log.w(LOG_TAG, "requestPermissions()");
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    private fun checkPermissions(): Boolean{
        return ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PERMISSION_REQUEST_ACCESS_LOCATION)
        {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(applicationContext, "Разрешение предоставлено", Toast.LENGTH_SHORT).show()
                getLastKnownLocation()
            } else {
                Toast.makeText(applicationContext, "Отказано пользователем", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isLocationEnabled(): Boolean{
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

}