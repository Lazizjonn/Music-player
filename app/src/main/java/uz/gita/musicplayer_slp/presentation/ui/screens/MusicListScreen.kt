package uz.gita.musicplayer_slp.presentation.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import uz.gita.musicplayer_slp.R
import uz.gita.musicplayer_slp.data.model.common.ActionEnum
import uz.gita.musicplayer_slp.data.model.common.MusicData
import uz.gita.musicplayer_slp.databinding.ScreenMusicListBinding
import uz.gita.musicplayer_slp.presentation.service.MyService
import uz.gita.musicplayer_slp.presentation.ui.adapter.MyCursorAdapter
import uz.gita.musicplayer_slp.utils.MyAppManager
import uz.gita.musicplayer_slp.utils.getMusicDataByPosition


class MusicListScreen : Fragment(R.layout.screen_music_list) {
    private val binding by viewBinding(ScreenMusicListBinding::bind)
    private val adapter = MyCursorAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setAdapter()
        liveData()
        clicks()
        firstMusicToBottomPart()
    }


    private fun setAdapter() {
        adapter.cursor = MyAppManager.cursor
        binding.musicList.layoutManager = LinearLayoutManager(requireContext())
        binding.musicList.adapter = adapter
        adapter.setSelectMusicPosition {
            MyAppManager.selectMusicPos = it
            MyAppManager.currentTime = 0
            startMyService(ActionEnum.PLAY)
        }
    }
    private fun liveData () {
        MyAppManager.playMusicLiveData.observe(viewLifecycleOwner, playMusicObserver)
        MyAppManager.isPlayingLiveData.observe(viewLifecycleOwner, isPlayingObserver)
    }
    private fun clicks() {
        binding.bottomPart.setOnClickListener {
            findNavController().navigate(MusicListScreenDirections.actionMusicListScreenToPlayScreen())
        }
        binding.buttonNextScreen.setOnClickListener { startMyService(ActionEnum.NEXT) }
        binding.buttonPrevScreen.setOnClickListener { startMyService(ActionEnum.PREV) }
        binding.buttonManageScreen.setOnClickListener { startMyService(ActionEnum.MANAGE) }
    }
    private fun firstMusicToBottomPart () {
        val musicData = MyAppManager.cursor?.getMusicDataByPosition(MyAppManager.selectMusicPos)!!
        binding.textMusicNameScreen.text = musicData.title!!
        binding.textArtistNameScreen.text = musicData.artist!!
        if (musicData.data != null) {
            if (getAlbumImage(musicData.data) == null) Glide
                .with(binding.imageMusic)
                .load(R.drawable.ic_music_disk)
                .into(binding.imageMusic)
            else Glide
                .with(binding.imageMusic)
                .load(getAlbumImage(musicData.data))
                .into(binding.imageMusic)
        }
    }


    private val playMusicObserver = Observer<MusicData> { data ->
        binding.textMusicNameScreen.text = data.title
        binding.textArtistNameScreen.text = data.artist
        if (data.data != null) {
            if (getAlbumImage(data.data) == null) Glide
                .with(binding.imageMusic)
                .load(R.drawable.ic_music_disk)
                .into(binding.imageMusic)
            else Glide
                .with(binding.imageMusic)
                .load(getAlbumImage(data.data))
                .into(binding.imageMusic)
        }
    }
    private val isPlayingObserver = Observer<Boolean> { bool ->
        if (bool) binding.buttonManageScreen.setImageResource(R.drawable.ic_pause)
        else binding.buttonManageScreen.setImageResource(R.drawable.ic_play)
    }


    private fun startMyService(action: ActionEnum) {
        val intent = Intent(requireContext(), MyService::class.java)
        intent.putExtra("COMMAND", action)
        if (Build.VERSION.SDK_INT >= 26) requireActivity().startForegroundService(intent)
        else requireActivity().startService(intent)
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