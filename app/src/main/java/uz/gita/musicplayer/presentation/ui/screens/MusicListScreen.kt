package uz.gita.musicplayer.presentation.ui.screens

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import uz.gita.musicplayer.R
import uz.gita.musicplayer.databinding.ScreenMusicListBinding
import uz.gita.musicplayer.presentation.service.MyService
import uz.gita.musicplayer.presentation.ui.adapter.MyCursorAdapter
import uz.gita.musicplayer.utils.MyAppManager


class MusicListScreen : Fragment(R.layout.screen_music_list) {
    private val binding by viewBinding(ScreenMusicListBinding::bind)
    private val adapter = MyCursorAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter.cursor = MyAppManager.cursor
        binding.musicList.layoutManager = LinearLayoutManager(requireContext())
        binding.musicList.adapter = adapter

        adapter.setSelectMusicPosition {
            MyAppManager.selectMusicPos = it
            startMyService()
        }
    }

    private fun startMyService() {
        val intent = Intent(requireContext(), MyService::class.java)
        if (Build.VERSION.SDK_INT >= 26) requireActivity().startForegroundService(intent)
        else requireActivity().startService(intent)
    }
}