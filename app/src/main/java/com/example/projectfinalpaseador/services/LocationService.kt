package com.example.projectfinalpaseador.services

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.os.Looper
import com.example.projectfinalpaseador.data.api.RetrofitClient
import com.example.projectfinalpaseador.data.model.LocationRequest
import com.example.projectfinalpaseador.data.repository.TokenRepository
import com.google.android.gms.location.*
import kotlinx.coroutines.*
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.projectfinalpaseador.MainActivity
import com.example.projectfinalpaseador.R

class LocationService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var tokenRepository: TokenRepository

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "location_service_channel"
        private const val LOCATION_UPDATE_INTERVAL = 3 * 60 * 1000L // 3 minutos
    }

    override fun onCreate() {
        super.onCreate()

        tokenRepository = TokenRepository(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        createNotificationChannel()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    sendLocationToServer(location)
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            when (intent?.action) {
                "START_LOCATION_UPDATES" -> startLocationUpdates()
                "STOP_LOCATION_UPDATES" -> stopLocationUpdates()
                else -> {
                    // Acción no reconocida, detener servicio
                    stopSelf()
                }
            }
        } catch (e: Exception) {
            // Error al procesar comando, detener servicio
            stopSelf()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startLocationUpdates() {
        try {
            val notification = createNotification()
            startForeground(NOTIFICATION_ID, notification)

            val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
                interval = LOCATION_UPDATE_INTERVAL
                fastestInterval = LOCATION_UPDATE_INTERVAL / 2
                priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            // Verificar permisos antes de solicitar ubicación
            if (android.content.pm.PackageManager.PERMISSION_GRANTED ==
                androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            } else {
                // Sin permisos, detener servicio
                stopSelf()
            }
        } catch (e: Exception) {
            // Error al iniciar actualizaciones de ubicación
            stopSelf()
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        stopForeground(true)
        stopSelf()
    }

    private fun sendLocationToServer(location: Location) {
        serviceScope.launch {
            try {
                val token = tokenRepository.getToken()
                if (!token.isNullOrBlank()) {
                    val locationRequest = LocationRequest(
                        latitude = location.latitude.toString(),
                        longitude = location.longitude.toString()
                    )

                    val authHeader = "Bearer $token"
                    val response = RetrofitClient.getApiService(this@LocationService)
                        .sendLocation(authHeader, locationRequest)

                    // Si hay error de autenticación, detener el servicio
                    if (response.code() == 401) {
                        stopLocationUpdates()
                    }
                }
            } catch (e: Exception) {
                // Log error but continue service - network issues are temporary
                e.printStackTrace()
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Servicio de Ubicación",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Enviando ubicación mientras trabajas"
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("Paseador Activo")
        .setContentText("Enviando tu ubicación...")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentIntent(
            PendingIntent.getActivity(
                this,
                0,
                Intent(this, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
        .build()

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}

