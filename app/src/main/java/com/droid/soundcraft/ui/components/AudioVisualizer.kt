package com.droid.soundcraft.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontVariation.width
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AudioVisualizer(progressFlow: Flow<Float>, amplitudeFlow: Flow<List<Int>>) {
    var audioLevels by remember {
        mutableStateOf(listOf<Int>())
    }
    val coroutineScope = rememberCoroutineScope()
    var playbackProgress by remember { mutableFloatStateOf(0f) }
    var waveFormProgressIndex by remember {
        mutableIntStateOf(0)
    }
    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.Default) {
            amplitudeFlow.collect {
                Log.d("AudioVisualizer", "amplitudes $it")
                audioLevels = it
            }
        }
        coroutineScope.launch(Dispatchers.IO) {
            progressFlow.collect {
                Log.d("AudioVisualizer", "progress $it")
                withContext(Dispatchers.Main) {
                    playbackProgress = it
                }
            }
        }
    }
    val state = rememberLazyListState()
    val scope = rememberCoroutineScope()
    LaunchedEffect(key1 = playbackProgress) {
        scope.launch {
            try {
                state.animateScrollToItem(waveFormProgressIndex +5)
            } catch (e: Exception) {

            }

        }

    }
    LazyRow(
        state = state,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        itemsIndexed(audioLevels) { index, level ->
            val waveFormProgress = (index.toFloat() / audioLevels.size) * 100
            Log.d("TAG", "AudioVisualizer: $index ${(index.toFloat() / audioLevels.size) * 100}")
            Box(
                modifier = Modifier
                    .background(
                        color =
                        if (waveFormProgress <= playbackProgress) {
                            waveFormProgressIndex = index
                            Color.Red
                        } else
                            Color.Gray

                    )
                    .fillMaxWidth()
            ) {
                Spacer(
                    modifier = Modifier
                        .height(( (level.toFloat() / audioLevels.max()) * 500  ).dp)
                        .width(10.dp)
                )
            }
        }
    }
}

@Composable
fun AudioVisualizer(amplitudeFlow: Flow<Int>) {
    var audioLevels by remember {
        mutableStateOf(listOf<Int>())
    }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.Default) {
            amplitudeFlow.collect {
                Log.d("AudioVisualizer", "amplitudes $it")
                audioLevels = audioLevels.toMutableList().apply {
                    add(it)
                }
            }
        }
    }
    val state = rememberLazyListState()
    val scope = rememberCoroutineScope()
    LaunchedEffect(audioLevels) {
        scope.launch {
            try {
                state.animateScrollToItem(audioLevels.size)
            } catch (e: Exception) {

            }

        }

    }
    LazyRow(
        state = state,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        itemsIndexed(audioLevels) { index, level ->
            Box(
                modifier = Modifier
                    .background(
                        color =
                        Color.Red

                    )
                    .fillMaxWidth()
            ) {
                Spacer(
                    modifier = Modifier
                        .height(( (level.toFloat() / audioLevels.max()) * 500  ).dp)
                        .width(10.dp)
                )
            }
        }
    }
}

