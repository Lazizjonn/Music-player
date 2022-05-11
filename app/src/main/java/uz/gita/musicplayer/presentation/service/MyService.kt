package uz.gita.musicplayer.presentation.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import uz.gita.musicplayer.R
import uz.gita.musicplayer.utils.MyAppManager
import uz.gita.musicplayer.utils.getMusicDataByPosition

class MyService : Service() {
    private val CHANNEL = "DEMO"

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createChannel()
        createForegroundService()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun createForegroundService() {
        val notification = NotificationCompat.Builder(this, CHANNEL)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Music Player")
            .setContent(createRemoteView())
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
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

    private fun createRemoteView(): RemoteViews {
        val view = RemoteViews(this.packageName, R.layout.remote_view)
        val musicData = MyAppManager.cursor?.getMusicDataByPosition(MyAppManager.selectMusicPos)!!
        view.setTextViewText(R.id.textMusicName, musicData.title)
        view.setTextViewText(R.id.textArtistName, musicData.artist)
        return view
    }
}