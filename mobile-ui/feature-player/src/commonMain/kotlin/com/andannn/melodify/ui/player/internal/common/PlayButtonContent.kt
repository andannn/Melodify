package com.andannn.melodify.ui.player.internal.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andannn.melodify.domain.model.PlayerState
import com.andannn.melodify.shared.compose.common.theme.MelodifyTheme

@Composable
internal fun PlayButtonContent(
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
                CircularProgressIndicator(modifier = Modifier.size(36.dp))
            }
        }
    }
}

@Preview
@Composable
private fun PlayButtonContentPreview1() {
    MelodifyTheme {
        PlayButtonContent(playerState = PlayerState.PLAYING)
    }
}

@Preview
@Composable
private fun PlayButtonContentPreview2() {
    MelodifyTheme {
        PlayButtonContent(playerState = PlayerState.PAUSED)
    }
}

@Preview
@Composable
private fun PlayButtonContentPreview3() {
    MelodifyTheme {
        PlayButtonContent(playerState = PlayerState.BUFFERING)
    }
}
