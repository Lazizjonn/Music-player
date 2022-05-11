package uz.gita.musicplayer.presentation.ui.adapter

import android.database.Cursor
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.gita.musicplayer.databinding.ItemMusicBinding
import uz.gita.musicplayer.utils.getMusicDataByPosition

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

}