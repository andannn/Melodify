/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.player.internal.port.player.control

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andannn.melodify.domain.model.PlayMode
import com.andannn.melodify.shared.compose.common.theme.MelodifyTheme
import com.andannn.melodify.shared.compose.common.widgets.LinerWaveSlider
import com.andannn.melodify.shared.compose.common.widgets.MarqueeText
import com.andannn.melodify.shared.compose.components.play.control.PlayerUiEvent
import com.andannn.melodify.ui.player.internal.common.PlayControlButtons
import com.andannn.melodify.ui.player.internal.port.player.MaxImagePaddingStart
import com.andannn.melodify.ui.player.internal.util.formatDuration
import kotlin.math.roundToLong

@Composable
internal fun LargePlayerControlArea(
    title: String,
    artist: String,
    modifier: Modifier = Modifier,
    progress: Float,
    duration: Long,
    enable: Boolean = true,
    isPlaying: Boolean = false,
    playMode: PlayMode = PlayMode.REPEAT_ALL,
    isShuffle: Boolean = false,
    onEvent: (PlayerUiEvent) -> Unit = {},
) {
    val titleState by rememberUpdatedState(newValue = title)
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(vertical = 8.dp),
    ) {
        Column(
            modifier = Modifier.weight(0.7f),
        ) {
            MarqueeText(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = MaxImagePaddingStart),
                text = titleState,
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                modifier = Modifier.padding(horizontal = MaxImagePaddingStart),
                text = artist,
                maxLines = 2,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .weight(1f),
            verticalArrangement = Arrangement.Center,
        ) {
            LinerWaveSlider(
                modifier = Modifier.fillMaxWidth(),
                value = progress,
                playing = isPlaying,
                onValueChange = {
                    onEvent(PlayerUiEvent.OnProgressChange(it))
                },
            )
            Spacer(Modifier.height(3.dp))
            Row {
                val durationString =
                    remember(duration) {
                        formatDuration(duration)
                    }
                val progressString =
                    remember(progress, duration) {
                        formatDuration((progress * duration).roundToLong())
                    }
                Text(progressString, style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.weight(1f))
                Text(durationString, style = MaterialTheme.typography.labelLarge)
            }
        }
        PlayControlButtons(
            modifier =
                Modifier
                    .padding(horizontal = 10.dp)
                    .weight(1.3f),
            enable = enable,
            isPlaying = isPlaying,
            playMode = playMode,
            isShuffle = isShuffle,
            onEvent = onEvent,
        )
    }
}

@Preview
@Composable
private fun LargePlayerControlAreaPreview() {
    MelodifyTheme {
        Surface(modifier = Modifier.height(300.dp)) {
            LargePlayerControlArea(
                title = "title",
                artist = "artist",
                progress = 0.5f,
                duration = 10,
            )
        }
    }
}
