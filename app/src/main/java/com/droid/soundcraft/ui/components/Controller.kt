package com.droid.soundcraft.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.droid.soundcraft.R
import kotlinx.coroutines.flow.Flow

@Composable
fun Controller(isActive: Flow<Boolean>, start: () -> Unit, pause: () -> Unit, stop: () -> Unit) {
    val isPlaying by isActive.collectAsState(initial = false)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
    ) {
        Row(
            modifier = Modifier.then(Modifier.align(Alignment.Center)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(color = Color.Red, shape = RoundedCornerShape(60.dp))
                    .clip(RoundedCornerShape(60.dp))
                    .clickable {
                        if (isPlaying) {
                            pause()
                        } else {
                            start()
                        }
                    }
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(35.dp),
                    painter = painterResource(
                        id = if (isPlaying) R.drawable.pause else R.drawable.play
                    ),
                    tint = Color.White,
                    contentDescription = "Recording State"
                )
            }
            Icon(
                modifier = Modifier
                    .size(30.dp)
                    .weight(1f)
                    .clickable { stop() },
                painter = painterResource(id = R.drawable.stop),
                contentDescription = "Stop"
            )
        }

    }

}