package com.droid.soundcraft.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RecordButton(modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(color = Color.White, shape = RoundedCornerShape(60.dp))
            .clip(RoundedCornerShape(60.dp))
            .then(modifier)
            .clickable { onClick() }
    ) {
        Spacer(modifier = Modifier.size(60.dp))
        Box(
            modifier = Modifier
                .background(color = Color.Red, shape = RoundedCornerShape(10.dp))
                .then(modifier)
        ) {
            Spacer(modifier = Modifier.size(20.dp))
        }
    }
}