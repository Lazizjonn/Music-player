package uz.gita.musicplayer.presentation.ui.service

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

/*
   Author: Zukhriddin Kamolov
   Created: 11.05.2022 at 22:09
   Project: MusicPlayer
*/

class MyService: Service() {
    private val CHANNEL = "DEMO"

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        creatChannel()
        foregroundService()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY

    }


    fun foregroundService(){
        val notification = NotificationCompat.Builder(this, CHANNEL)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("MusicPlayer")
            .setContent(remoteView())
/*            .setPriority(NotificationCompat.PRIORITY_MIN)*/
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .build()

        startForeground(1, notification)

    }

    private fun remoteView(): RemoteViews {
        val view = RemoteViews(this.packageName, R.layout.remote_view)
        val musicData = MyAppManager.cursor?.getMusicDataByPosition(MyAppManager.musicPosition)!!
        view.setTextViewText(R.id.textMusicName, musicData.title)
        view.setTextViewText(R.id.textArtistName, musicData.artist)
        return view
    }

    private fun creatChannel(){
        if(Build.VERSION.SDK_INT >= 26){
            val channel = NotificationChannel("DEMO", CHANNEL, NotificationManager.IMPORTANCE_DEFAULT)
            val service = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            service.createNotificationChannel(channel)
        }
    }



}