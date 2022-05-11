package uz.gita.musicplayer.presentation.ui.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import uz.gita.musicplayer.R
import uz.gita.musicplayer.databinding.ScreenMusicListBinding


class MusicListScreen : Fragment(R.layout.screen_music_list) {
    private val binding by viewBinding(ScreenMusicListBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

}