package uz.gita.musicplayer_slp.presentation.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import uz.gita.musicplayer_slp.R
import uz.gita.musicplayer_slp.data.model.common.ActionEnum
import uz.gita.musicplayer_slp.data.model.common.MusicData
import uz.gita.musicplayer_slp.utils.MyAppManager
import uz.gita.musicplayer_slp.utils.getMusicDataByPosition
import java.io.File

class MyService : Service() {
    override fun onBind(p0: Intent?): IBinder? = null
    private val CHANNEL = "DEMO"
    private var _mediaPlayer: MediaPlayer? = null
    private val mediaPlayer get() = _mediaPlayer!!
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private var job: Job? = null
    private var jobSelection: Job? = null
    private val scopeSelection = CoroutineScope(Dispatchers.Main + Job())


    override fun onCreate() {
        super.onCreate()
        _mediaPlayer = MediaPlayer()
        createChannel()
        createForegroundService()
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val command = intent!!.extras?.getSerializable("COMMAND") as ActionEnum
        jobSelection?.cancel()
        jobSelection = scopeSelection.launch {
            delay(200)
            doneCommand(command)
        }
        return START_NOT_STICKY
    }
    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
        jobSelection?.cancel()
    }


    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel("DEMO", CHANNEL, NotificationManager.IMPORTANCE_DEFAULT)
            channel.setSound(null, null)
            val service = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(channel)
        }
    }
    private fun createForegroundService() {
        val notification = NotificationCompat.Builder(this, CHANNEL)
            .setSmallIcon(R.drawable.musicpicture)
            .setContentTitle("Music Player")
            .setContent(createRemoteView())
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .build()
        startForeground(1, notification)
    }
    private fun createRemoteView(): RemoteViews {
        val view = RemoteViews(this.packageName, R.layout.remote_view)
        val musicData = MyAppManager.cursor?.getMusicDataByPosition(MyAppManager.selectMusicPos)!!
        view.setTextViewText(R.id.textMusicName, musicData.title)
        view.setTextViewText(R.id.textArtistName, musicData.artist)

        if (mediaPlayer.isPlaying) {
            view.setImageViewResource(R.id.buttonManage, R.drawable.ic_pause)
        } else view.setImageViewResource(R.id.buttonManage, R.drawable.ic_play)

        view.setOnClickPendingIntent(R.id.buttonPrev, createPendingIntent(ActionEnum.PREV))
        view.setOnClickPendingIntent(R.id.buttonNext, createPendingIntent(ActionEnum.NEXT))
        view.setOnClickPendingIntent(R.id.buttonManage, createPendingIntent(ActionEnum.MANAGE))
        view.setOnClickPendingIntent(R.id.buttonCancel, createPendingIntent(ActionEnum.CANCEL))

        return view
    }
    private fun createPendingIntent(action: ActionEnum): PendingIntent {
        val intent = Intent(this, MyService::class.java)
        intent.putExtra("COMMAND", action)
        return PendingIntent.getService(
            this,
            action.pos,
            intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
    private fun doneCommand(lastCommand: ActionEnum) {
        val data: MusicData = MyAppManager.cursor?.getMusicDataByPosition(MyAppManager.selectMusicPos)!!
        when (lastCommand) {
            ActionEnum.MANAGE -> {
                if (mediaPlayer.isPlaying) doneCommand(ActionEnum.PAUSE)
                else doneCommand(ActionEnum.PLAY)
            }
            ActionEnum.PLAY -> {
                /*   if (mediaPlayer.isPlaying) {
                       mediaPlayer.stop()
                       _mediaPlayer = null
                   }*/
                if (_mediaPlayer != null) {
                    if (mediaPlayer.isPlaying) mediaPlayer.stop()
                    mediaPlayer.reset()
                    mediaPlayer.release()
                    _mediaPlayer = null
                }
                _mediaPlayer = MediaPlayer.create(this, Uri.fromFile(File(data.data ?: "")))
                MyAppManager.mediaPlayer = mediaPlayer
                mediaPlayer.start()
                mediaPlayer.setOnCompletionListener {
                    if (MyAppManager.isPlaying) {
                        doneCommand(ActionEnum.NEXT)
                    }
                }
                MyAppManager.fullTime = data.duration!!
                mediaPlayer.seekTo(MyAppManager.currentTime.toInt())

                job?.cancel()
                job = scope.launch {
                    changeProgress().collectLatest {
                        MyAppManager.currentTime = it
                        MyAppManager.currentTimeLiveData.postValue(it)
                    }
                }

                MyAppManager.isPlaying = true
                MyAppManager.isPlayingLiveData.value = true
                MyAppManager.playMusicLiveData.value = data
                createForegroundService()
            }
            ActionEnum.PAUSE -> {
                mediaPlayer.stop()
                job?.cancel()
                MyAppManager.isPlaying = false
                MyAppManager.isPlayingLiveData.value = false
                createForegroundService()
            }
            ActionEnum.NEXT -> {
                if (MyAppManager.selectMusicPos == MyAppManager.cursor!!.count - 1) {
                    MyAppManager.selectMusicPos = 0
                    MyAppManager.currentTime = 0
                    doneCommand(ActionEnum.PLAY)
                } else {
                    MyAppManager.selectMusicPos++
                    MyAppManager.currentTime = 0
                    doneCommand(ActionEnum.PLAY)
                }
            }
            ActionEnum.PREV -> {
                if (MyAppManager.selectMusicPos == 0) {
                    MyAppManager.selectMusicPos = MyAppManager.cursor!!.count - 1
                    MyAppManager.currentTime = 0
                    doneCommand(ActionEnum.PLAY)
                } else {
                    MyAppManager.selectMusicPos--
                    MyAppManager.currentTime = 0
                    doneCommand(ActionEnum.PLAY)
                }
            }
            ActionEnum.CANCEL -> {
                if (_mediaPlayer != null) {
                    if (mediaPlayer.isPlaying) mediaPlayer.stop()
                    mediaPlayer.reset()
                    mediaPlayer.release()
                    _mediaPlayer = null
                }
                MyAppManager.selectMusicPos = 0
                MyAppManager.isPlaying = false
                MyAppManager.isPlayingLiveData.value = false
                stopSelf()
            }
        }
    }
    private fun changeProgress(): Flow<Long> = flow {
        MyAppManager.currentTimeLiveData.postValue(MyAppManager.currentTime)
        while ((MyAppManager.currentTimeLiveData.value ?: 0) <= MyAppManager.fullTime) {
            delay(1000)
            MyAppManager.currentTime += 1000
            MyAppManager.currentTimeLiveData.postValue(MyAppManager.currentTime)
            emit(MyAppManager.currentTime)
        }
    }
}