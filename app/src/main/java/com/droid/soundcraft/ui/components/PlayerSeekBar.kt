package com.droid.soundcraft.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.droid.soundcraft.core.formatMillisecondsToHMS
import kotlinx.coroutines.flow.Flow


@Composable
fun PlayerSeekBar(currentPosition: Flow<Int>, duration: Int) {
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    var currentPositionHMS by remember {
        mutableStateOf("00:00")
    }
    LaunchedEffect(Unit) {
        currentPosition.collect {
            Log.d("PlayerSeekBar", "PlayerSeekBar: $it")
            currentPositionHMS = it.formatMillisecondsToHMS()
            sliderPosition = it.toFloat()
        }
    }
    Column {
        Slider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            valueRange = 0f..duration.toFloat(),
            enabled = false
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = currentPositionHMS)
            Text(text = duration.formatMillisecondsToHMS())
        }
    }

}