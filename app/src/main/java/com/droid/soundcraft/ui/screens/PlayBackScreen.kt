package com.droid.soundcraft.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.droid.soundcraft.core.AudioPlayer
import com.droid.soundcraft.core.formatMillisecondsToHMS
import com.droid.soundcraft.ui.components.AudioVisualizer
import com.droid.soundcraft.ui.components.PlayerSeekBar
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayBackScreen(audioPlayer: AudioPlayer, onBackPress: () -> Unit) {

    DisposableEffect(key1 = Unit) {

        onDispose {
            audioPlayer.release()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "PlayBack")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        audioPlayer.stop()
                        audioPlayer.release()
                        onBackPress()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(20.dp),

        ) {
            Box(modifier = Modifier.weight(1f)) {
                AudioVisualizer(amplitudeFlow = audioPlayer.getAmplitudes(), progressFlow = audioPlayer.getProgress())
            }
            PlayerSeekBar(audioPlayer.getCurrentPosition(),audioPlayer.getDuration())
            Controller(audioPlayer.getIsPlaying(),audioPlayer::start, audioPlayer::pause) {
                audioPlayer.stop()
                audioPlayer.release()
                onBackPress()
            }
        }
    }
}