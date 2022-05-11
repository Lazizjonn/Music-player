package uz.gita.musicplayer.presentation.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.gita.musicplayer.R
import uz.gita.musicplayer.utils.checkPermissions
import uz.gita.musicplayer.utils.getMusicsCursor

@SuppressLint("CustomSplashScreen")
class SplashScreen : Fragment(R.layout.screen_splash) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().checkPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            requireContext().getMusicsCursor().onEach {

            }.launchIn(lifecycleScope)
        }
    }
}