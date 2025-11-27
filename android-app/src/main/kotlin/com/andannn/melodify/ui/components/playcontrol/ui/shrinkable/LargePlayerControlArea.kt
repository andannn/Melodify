/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.playcontrol.ui.shrinkable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.ShuffleOn
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andannn.melodify.core.data.model.PlayMode
import com.andannn.melodify.core.platform.formatTime
import com.andannn.melodify.ui.components.playcontrol.PlayerUiEvent
import com.andannn.melodify.ui.util.formatDuration
import com.andannn.melodify.ui.util.getIcon
import com.andannn.melodify.ui.widgets.LinerWaveSlider
import com.andannn.melodify.ui.widgets.MarqueeText
import com.andannn.melodify.ui.widgets.SmpMainIconButton
import com.andannn.melodify.ui.widgets.SmpSubIconButton
import kotlin.math.roundToLong
import kotlin.time.Duration.Companion.milliseconds

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
    onEvent: (PlayerUiEvent) -> Unit = {},
    isShuffle: Boolean = false,
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
