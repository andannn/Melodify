package com.andannn.melodify.ui.player.internal.common

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.andannn.melodify.domain.model.PlayerState

@Composable
fun PlayButtonContent(
    playerState: PlayerState,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        when (playerState) {
            PlayerState.PLAYING -> {
                Icon(imageVector = Icons.Rounded.Pause, contentDescription = "")
            }

            PlayerState.PAUSED -> {
                Icon(imageVector = Icons.Rounded.PlayArrow, contentDescription = "")
            }

            PlayerState.BUFFERING -> {
                CircularProgressIndicator()
            }
        }
    }
}
