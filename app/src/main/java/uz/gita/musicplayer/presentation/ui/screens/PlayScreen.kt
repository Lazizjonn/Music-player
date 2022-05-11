package uz.gita.musicplayer.presentation.ui.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import uz.gita.musicplayer.R
import uz.gita.musicplayer.databinding.ScreenPlayBinding

class PlayScreen : Fragment(R.layout.screen_play) {
    private val binding by viewBinding(ScreenPlayBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

}