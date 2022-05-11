package uz.gita.musicplayer.presentation.ui.screens

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import uz.gita.musicplayer.R
import uz.gita.musicplayer.databinding.ScreenMusicListBinding
import uz.gita.musicplayer.presentation.ui.adapter.MyCursoreAdapter
import uz.gita.musicplayer.presentation.ui.service.MyService
import uz.gita.musicplayer.utils.MyAppManager
import uz.gita.musicplayer.utils.getMusicDataByPosition


class MusicListScreen : Fragment(R.layout.screen_music_list) {
    private val binding by viewBinding(ScreenMusicListBinding::bind)
    private val myCursoreAdapter = MyCursoreAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setAdapter()
        clicks()

    }

    private fun clicks() {
        myCursoreAdapter.setMusicPositionListener {
            MyAppManager.musicPosition = it
            startMyService()
            Snackbar.make(requireView(), "Music: " + MyAppManager.cursor?.getMusicDataByPosition(it)?.title, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun setAdapter() {
        myCursoreAdapter.cursore = MyAppManager.cursor
        binding.musicList.layoutManager = LinearLayoutManager(requireContext())
        binding.musicList.adapter = myCursoreAdapter
    }

    private fun startMyService(){
        val intent = Intent(requireContext(), MyService::class.java)
        if (Build.VERSION.SDK_INT >= 26){
            requireActivity().startForegroundService(intent)
        } else requireActivity().startService(intent)

    }

}