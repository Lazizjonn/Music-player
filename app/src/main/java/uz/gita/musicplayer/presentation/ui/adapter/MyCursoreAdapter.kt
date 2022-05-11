package uz.gita.musicplayer.presentation.ui.adapter

import android.database.Cursor
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.gita.musicplayer.databinding.ItemMusicBinding
import uz.gita.musicplayer.utils.getMusicDataByPosition

/*
   Author: Zukhriddin Kamolov
   Created: 11.05.2022 at 16:11
   Project: MusicPlayer
*/

class MyCursoreAdapter: RecyclerView.Adapter<MyCursoreAdapter.CursoreViewHolder>() {
    var cursore: Cursor? = null
    private var getMusicPositionListener: ((Int)->Unit)? = null

    inner class CursoreViewHolder(private val binding: ItemMusicBinding): RecyclerView.ViewHolder(binding.root){

        init {
            binding.root.setOnClickListener {
                getMusicPositionListener?.invoke(absoluteAdapterPosition)
            }
        }

        fun bind(){
            cursore?.getMusicDataByPosition(absoluteAdapterPosition).let {
                binding.textMusicName.text = it?.title
                binding.textArtistName.text = it?.artist
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CursoreViewHolder {
        return CursoreViewHolder(ItemMusicBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: CursoreViewHolder, position: Int) = holder.bind()

    override fun getItemCount(): Int = cursore?.count ?: 0


    fun setMusicPositionListener(block: (Int)-> Unit){
        getMusicPositionListener = block
    }
}