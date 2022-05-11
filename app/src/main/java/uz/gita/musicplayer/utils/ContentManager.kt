package uz.gita.musicplayer.utils

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uz.gita.musicplayer.data.model.common.MusicData

private val projection = arrayOf(
    MediaStore.Audio.Media._ID,
    MediaStore.Audio.Media.ARTIST,
    MediaStore.Audio.Media.TITLE,
    MediaStore.Audio.Media.DATA,
    MediaStore.Audio.Media.DURATION
)

fun Context.getMusicCursor(): Flow<Cursor> = flow {
    val cursor: Cursor = contentResolver.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        projection,
        MediaStore.Audio.Media._ID + "!=0",
        null,
        null
    ) ?: return@flow
    emit(cursor)
}


fun Cursor.getMusicDataByPosition(position: Int): MusicData{
    this.moveToPosition(position)
    return MusicData(this.getInt(0),this.getString(1),this.getString(2),this.getString(3),this.getLong(4))
}