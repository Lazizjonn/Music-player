package uz.gita.musicplayer.utils

import android.database.Cursor
import android.media.MediaPlayer
import androidx.lifecycle.MutableLiveData
import uz.gita.musicplayer.data.model.common.MusicData

object MyAppManager {
    var selectMusicPos: Int = -1
    var cursor: Cursor? = null
    var currentTime: Long = 0L
    var fullTime: Long = 0L
    var isPlaying = false
    var mediaPlayer = MediaPlayer()

    val currentTimeLiveData = MutableLiveData<Long>()
    val playMusicLiveData = MutableLiveData<MusicData>()
    val isPlayingLiveData = MutableLiveData<Boolean>()
}