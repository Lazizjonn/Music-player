package uz.gita.musicplayer_slp.presentation.ui.adapter

import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import uz.gita.musicplayer_slp.databinding.ItemMusicBinding
import uz.gita.musicplayer_slp.utils.getMusicDataByPosition

class MyCursorAdapter() : RecyclerView.Adapter<MyCursorAdapter.MyCursorViewHolder>() {
    var cursor: Cursor? = null
    private var selectMusicPosition: ((Int) -> Unit)? = null

    inner class MyCursorViewHolder(private val binding: ItemMusicBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                selectMusicPosition?.invoke(absoluteAdapterPosition)
            }
        }
        fun bind() {
            cursor?.getMusicDataByPosition(absoluteAdapterPosition)?.let {
                binding.textMusicName.text = it.title
                binding.textArtistName.text = it.artist
                if (it.data != null) {
                    getAlbumImage(it.data)?.let { it2 ->
                        Glide
                            .with(binding.imageMusic)
                            .load(it2)
                            .into(binding.imageMusic)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCursorViewHolder =
        MyCursorViewHolder(ItemMusicBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(holder: MyCursorViewHolder, position: Int) = holder.bind()
    override fun getItemCount(): Int = cursor?.count ?: 0

    fun setSelectMusicPosition(block: (Int) -> Unit) {
        selectMusicPosition = block
    }
    private fun getAlbumImage(path: String): Bitmap? {
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(path)
        val data: ByteArray? = mmr.embeddedPicture
        return when {
            data != null -> BitmapFactory.decodeByteArray(data, 0, data.size)
                    else -> null
        }
    }
}