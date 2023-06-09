package uz.gita.musicplayer_slp.presentation.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.gita.musicplayer_slp.R
import uz.gita.musicplayer_slp.utils.MyAppManager
import uz.gita.musicplayer_slp.utils.checkPermissions
import uz.gita.musicplayer_slp.utils.getMusicsCursor

@SuppressLint("CustomSplashScreen")
class SplashScreen : Fragment(R.layout.screen_splash) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().checkPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            requireContext().getMusicsCursor().onEach {
                MyAppManager.cursor = it
                MyAppManager.selectMusicPos = 0
                findNavController().navigate(SplashScreenDirections.actionSplashScreenToMusicListScreen())
            }.launchIn(lifecycleScope)
        }
    }
}