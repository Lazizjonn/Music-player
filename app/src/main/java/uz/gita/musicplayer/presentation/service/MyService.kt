package uz.gita.musicplayer.presentation.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import uz.gita.musicplayer.R

class MyService : Service() {

    override fun onBind(p0: Intent?): IBinder? = null
    private val CHANNEL = "DEMO"

    override fun onCreate() {
        super.onCreate()
        createChannel()
        createForegroundService()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }


    private fun createForegroundService() {
        val notification = NotificationCompat.Builder(this, CHANNEL)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Music Player")
            .setContentText("")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        startForeground(1, notification)
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel("DEMO", CHANNEL, NotificationManager.IMPORTANCE_DEFAULT)
            val service = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(channel)
        }
    }
}