package uz.gita.musicplayer_slp.presentation.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import uz.gita.musicplayer_slp.R
import uz.gita.musicplayer_slp.data.model.common.ActionEnum
import uz.gita.musicplayer_slp.data.model.common.MusicData
import uz.gita.musicplayer_slp.databinding.ScreenPlayBinding
import uz.gita.musicplayer_slp.presentation.service.MyService
import uz.gita.musicplayer_slp.utils.MyAppManager
import uz.gita.musicplayer_slp.utils.MyAppManager.currentTime

class PlayScreen : Fragment(R.layout.screen_play) {
    private val binding by viewBinding(ScreenPlayBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        clicksAndSeekBarChange()
        liveData()
    }

    private fun clicksAndSeekBarChange() {
        binding.buttonNext.setOnClickListener { startMyService(ActionEnum.NEXT) }
        binding.buttonPrev.setOnClickListener { startMyService(ActionEnum.PREV) }
        binding.buttonManage.setOnClickListener { startMyService(ActionEnum.MANAGE) }
        binding.back.setOnClickListener { findNavController().navigateUp() }
        binding.seekBarScreen.setOnSeekBarChangeListener(SeekBarListener)
    }
    private fun liveData() {
        MyAppManager.playMusicLiveData.observe(viewLifecycleOwner, playMusicObserver)
        MyAppManager.isPlayingLiveData.observe(viewLifecycleOwner, isPlayingObserver)
        MyAppManager.currentTimeLiveData.observe(viewLifecycleOwner, currentTimeObserver)
    }

    private val isPlayingObserver = Observer<Boolean> {
        if (it) binding.buttonManage.setImageResource(R.drawable.ic_pause)
        else binding.buttonManage.setImageResource(R.drawable.ic_play)
    }
    private val playMusicObserver = Observer<MusicData> {
        binding.seekBarScreen.max = it.duration!!.toInt()
        binding.seekBarScreen.progress = (currentTime).toInt()
        binding.textMusicName.text = it.title
        binding.textArtistName.text = it.artist
        binding.currentTime.text = currentTime.toString()
        if (it.data != null) getAlbumImage(it.data)?.let { it2 ->
            Glide
                .with(binding.pictureOfMusic)
                .load(it2)
                .into(binding.pictureOfMusic)
        }
        val itDurationDiv1000Div60 = it.duration.div(1000).div(60)
        val itDurationDiv1000Rem60 = it.duration.div(1000).rem(60)
        val time: String = if ((itDurationDiv1000Div60) < 10) {
            if ((itDurationDiv1000Rem60) < 10) ("0$itDurationDiv1000Div60") + ":0" + (itDurationDiv1000Rem60).toString()
            else ("0$itDurationDiv1000Div60") + ":" + (itDurationDiv1000Rem60).toString()
        } else {
            if ((itDurationDiv1000Rem60) < 10) (itDurationDiv1000Div60).toString() + ":0" + (itDurationDiv1000Rem60).toString()
            else (itDurationDiv1000Div60).toString() + ":" + (itDurationDiv1000Rem60).toString()
        }
        binding.totalTime.text = time
    }
    private val currentTimeObserver = Observer<Long> {
        binding.seekBarScreen.progress = (currentTime).toInt()
        val currentTimeDiv1000Div60 = currentTime.div(1000).div(60)
        val currentTimeDiv1000Rem60 = currentTime.div(1000).rem(60)
        val time: String = if (currentTimeDiv1000Div60 < 10) {
            if (currentTimeDiv1000Rem60 < 10) ("0$currentTimeDiv1000Div60").toString() + ":0" + currentTimeDiv1000Rem60.toString()
            else ("0$currentTimeDiv1000Div60").toString() + ":" + currentTimeDiv1000Rem60.toString()

        } else {
            if (currentTimeDiv1000Rem60 < 10) (currentTimeDiv1000Div60).toString() + ":0" + currentTimeDiv1000Rem60.toString()
            else (currentTimeDiv1000Div60).toString() + ":" + currentTimeDiv1000Rem60.toString()
        }
        binding.currentTime.text = time
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


    private object SeekBarListener : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (fromUser) {
                currentTime = progress.toLong()
                MyAppManager.currentTimeLiveData.value = currentTime
                MyAppManager.mediaPlayer.seekTo(currentTime.toInt())
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    }
}