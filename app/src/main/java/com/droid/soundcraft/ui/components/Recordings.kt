package com.droid.soundcraft.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.io.File


@Composable
fun Recordings(modifier: Modifier, recordingFilesPath: List<File>,navigateToPlayback: (String) -> Unit) {
    Box(modifier = modifier.fillMaxSize()) {
        if (recordingFilesPath.isEmpty()) {
            Text(text = "Tap the record button to start", modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn {
                items(recordingFilesPath.size) { index ->
                    Recording(recordingFilesPath[index].name) {
                        navigateToPlayback(recordingFilesPath[index].absolutePath)
                    }
                }

            }
        }
    }

}


@Composable
fun Recording(title: String, onClick: () -> Unit) {
    val iconSize  = 20.dp
    Card(
        modifier = Modifier
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title)
            Box(
                modifier = Modifier
                    .size((iconSize * 2))
                    .background(color = Color.Red, shape = CircleShape)
                    .clickable { onClick() }
            ) {
                Icon(
                    modifier = Modifier
                        .size(iconSize)
                        .align(Alignment.Center),
                    imageVector = Icons.Default.PlayArrow,
                    tint = Color.White,
                    contentDescription = null,
                )
            }
        }

    }
}