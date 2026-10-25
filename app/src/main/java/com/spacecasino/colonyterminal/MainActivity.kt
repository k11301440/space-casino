package com.spacecasino.colonyterminal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.spacecasino.colonyterminal.ui.GameViewModel
import com.spacecasino.colonyterminal.ui.main.MainScreen

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainScreen(viewModel = viewModel)
        }
    }

    override fun onResume() {
        super.onResume()
        com.spacecasino.colonyterminal.audio.SoundManager.setBgmMode(viewModel.gameState.value.bgmMode)
    }

    override fun onPause() {
        super.onPause()
        viewModel.onAppPause()
        com.spacecasino.colonyterminal.audio.SoundManager.stopBgm()
    }

    override fun onStop() {
        super.onStop()
        viewModel.onAppPause()
        com.spacecasino.colonyterminal.audio.SoundManager.stopBgm()
    }
}
