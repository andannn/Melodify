/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.playcontrol

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Slider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.ShuffleOn
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.andannn.melodify.core.data.model.PlayMode
import com.andannn.melodify.core.data.model.subTitle
import com.andannn.melodify.core.platform.formatTime
import com.andannn.melodify.shared.compose.common.widgets.CircleBorderImage
import com.andannn.melodify.ui.util.getIcon
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun DesktopPlayerUi(
    state: PlayerUiState,
    modifier: Modifier = Modifier,
) {
    when (state) {
        is PlayerUiState.Active -> {
            PlayStateBar(
                modifier = modifier,
                coverUri = state.mediaItem.artWorkUri,
                playMode = state.playMode,
                isShuffle = state.isShuffle,
                isPlaying = state.isPlaying,
                isFavorite = state.isFavorite,
                title = state.mediaItem.name,
                artist = state.mediaItem.subTitle,
                progress = state.progress,
                duration = state.duration,
                onEvent = state.eventSink,
            )
        }

        is PlayerUiState.Inactive -> {
            PlayStateBar(
                modifier = modifier,
                coverUri = "",
                enabled = false,
            )
        }
    }
}

@Composable
private fun PlayStateBar(
    coverUri: String?,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    playMode: PlayMode = PlayMode.REPEAT_ALL,
    isShuffle: Boolean = false,
    isPlaying: Boolean = false,
    isFavorite: Boolean = false,
    title: String = "",
    artist: String = "",
    progress: Float = 0f,
    duration: Long = 0L,
    onEvent: (PlayerUiEvent) -> Unit = {},
) {
    Surface(
        modifier = modifier,
    ) {
        Column {
            Row(
                modifier = Modifier.height(48.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PlayInfoWithAlbumCover(
                    modifier = Modifier.weight(1f),
                    coverUri = coverUri,
                    title = title,
                    artist = artist,
                )

                PlayControlBar(
                    modifier = Modifier,
                    enabled = enabled,
                    isPlaying = isPlaying,
                    playMode = playMode,
                    isShuffle = isShuffle,
                    onEvent = onEvent,
                )

                Spacer(modifier = Modifier.weight(1f))
            }

            ProgressBar(
                modifier = Modifier.height(24.dp),
                enabled = enabled,
                progress = progress,
                duration = duration,
                onValueChange = {
                    onEvent(PlayerUiEvent.OnProgressChange(it))
                },
            )
        }
    }
}

@Composable
private fun PlayControlBar(
    modifier: Modifier = Modifier,
    isShuffle: Boolean,
    isPlaying: Boolean,
    enabled: Boolean,
    playMode: PlayMode,
    onEvent: (PlayerUiEvent) -> Unit = {},
) {
    Row(modifier = modifier) {
        IconButton(
            enabled = enabled,
            onClick = {
                onEvent(PlayerUiEvent.OnShuffleButtonClick)
            },
        ) {
            Icon(
                if (isShuffle) Icons.Rounded.ShuffleOn else Icons.Rounded.Shuffle,
                contentDescription = "",
            )
        }

        IconButton(
            enabled = enabled,
            onClick = {
                onEvent(PlayerUiEvent.OnNextButtonClick)
            },
        ) {
            Icon(
                Icons.Rounded.SkipPrevious,
                contentDescription = "",
            )
        }

        IconButton(
            enabled = enabled,
            onClick = {
                onEvent(PlayerUiEvent.OnPlayButtonClick)
            },
        ) {
            if (isPlaying) {
                Icon(imageVector = Icons.Rounded.Pause, contentDescription = "")
            } else {
                Icon(imageVector = Icons.Rounded.PlayArrow, contentDescription = "")
            }
        }

        IconButton(
            modifier = Modifier,
            enabled = enabled,
            onClick = {
                onEvent(PlayerUiEvent.OnNextButtonClick)
            },
        ) {
            Icon(
                Icons.Rounded.SkipNext,
                contentDescription = "",
            )
        }

        IconButton(
            modifier = Modifier,
            enabled = enabled,
            onClick = {
                onEvent(PlayerUiEvent.OnPlayModeButtonClick)
            },
        ) {
            Icon(
                imageVector = playMode.getIcon(),
                contentDescription = "",
            )
        }

        Spacer(modifier = Modifier.width(10.dp))
    }
}

@Composable
private fun PlayInfoWithAlbumCover(
    modifier: Modifier,
    coverUri: String?,
    title: String,
    artist: String,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (coverUri != null) {
            CircleBorderImage(
                modifier =
                    Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f),
                model = coverUri,
            )

            Spacer(modifier = Modifier.width(10.dp))
        }

        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = title,
                maxLines = 1,
                style = MaterialTheme.typography.bodyLarge,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = artist,
                maxLines = 1,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun ProgressBar(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    progress: Float = 0f,
    duration: Long = 0L,
    onValueChange: (Float) -> Unit = {},
) {
    val durationString by rememberUpdatedState(
        duration.milliseconds.toComponents { minutes, seconds, _ ->
            formatTime(minutes, seconds)
        },
    )

    val progressString by rememberUpdatedState(
        (duration * progress).toLong().milliseconds.toComponents { minutes, seconds, _ ->
            formatTime(minutes, seconds)
        },
    )

    Row(
        modifier = modifier.padding(start = 8.dp, end = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier =
                Modifier.graphicsLayer {
                    alpha = if (enabled) 1f else 0.5f
                },
            text = progressString,
            style = MaterialTheme.typography.bodySmall,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Slider(
            modifier = modifier.weight(1f),
            value = progress,
            enabled = enabled,
            onValueChange = onValueChange,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            modifier =
                Modifier.graphicsLayer {
                    alpha = if (enabled) 1f else 0.5f
                },
            text = durationString,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}
