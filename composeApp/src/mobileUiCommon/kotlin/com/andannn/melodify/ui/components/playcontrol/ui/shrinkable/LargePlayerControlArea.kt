/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.playcontrol.ui.shrinkable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.ShuffleOn
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.andannn.melodify.core.data.model.PlayMode
import com.andannn.melodify.ui.components.playcontrol.PlayerUiEvent
import com.andannn.melodify.ui.theme.MelodifyTheme
import com.andannn.melodify.ui.util.getIcon
import com.andannn.melodify.ui.widgets.LinerWaveSlider
import com.andannn.melodify.ui.widgets.MarqueeText
import com.andannn.melodify.ui.widgets.SmpMainIconButton
import com.andannn.melodify.ui.widgets.SmpSubIconButton
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun LargePlayerControlArea(
    title: String,
    artist: String,
    modifier: Modifier = Modifier,
    progress: Float = 0.5f,
    enable: Boolean = true,
    isPlaying: Boolean = false,
    playMode: PlayMode = PlayMode.REPEAT_ALL,
    onEvent: (PlayerUiEvent) -> Unit = {},
    isShuffle: Boolean = false,
) {
    val titleState by rememberUpdatedState(newValue = title)
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.weight(0.7f),
        ) {
            MarqueeText(
                modifier = Modifier.fillMaxWidth().padding(horizontal = MaxImagePaddingStart),
                text = titleState,
                spacingBetweenCopies = 40.dp,
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                modifier = Modifier.padding(horizontal = MaxImagePaddingStart),
                text = artist,
                maxLines = 2,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        LinerWaveSlider(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .weight(1f),
            value = progress,
            playing = isPlaying,
            onValueChange = {
                onEvent(PlayerUiEvent.OnProgressChange(it))
            },
        )
        Row(
            modifier =
                Modifier
                    .padding(horizontal = 10.dp)
                    .weight(1.4f),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SmpSubIconButton(
                modifier =
                    Modifier
                        .weight(1f)
                        .aspectRatio(1f),
                imageVector = if (isShuffle) Icons.Rounded.ShuffleOn else Icons.Rounded.Shuffle,
                enabled = enable,
                onClick = {
                    onEvent(PlayerUiEvent.OnShuffleButtonClick)
                },
            )
            SmpSubIconButton(
                modifier =
                    Modifier
                        .weight(1f)
                        .aspectRatio(1f),
                scale = 2f,
                enabled = enable,
                imageVector = Icons.Rounded.SkipPrevious,
                onClick = {
                    onEvent(PlayerUiEvent.OnPreviousButtonClick)
                },
            )

            SmpMainIconButton(
                modifier =
                    Modifier
                        .weight(1f)
                        .aspectRatio(1f),
                enabled = enable,
                imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                onClick = {
                    onEvent(PlayerUiEvent.OnPlayButtonClick)
                },
            )
            SmpSubIconButton(
                modifier =
                    Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .padding(10.dp),
                scale = 2f,
                enabled = enable,
                imageVector = Icons.Rounded.SkipNext,
                onClick = {
                    onEvent(PlayerUiEvent.OnNextButtonClick)
                },
            )
            SmpSubIconButton(
                modifier = Modifier.weight(1f),
                imageVector = playMode.getIcon(),
                enabled = enable,
                onClick = {
                    onEvent(PlayerUiEvent.OnPlayModeButtonClick)
                },
            )
        }
    }
}

@Preview
@Composable
private fun LargeControlAreaPreview() {
    MelodifyTheme(darkTheme = false) {
        Surface {
            LargePlayerControlArea(
                modifier = Modifier.width(530.dp).height(250.dp),
                title = "title",
                artist = "artist",
            )
        }
    }
}
