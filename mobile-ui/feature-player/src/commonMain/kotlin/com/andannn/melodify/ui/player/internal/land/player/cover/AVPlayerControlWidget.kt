/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.player.internal.land.player.cover

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBackIos
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andannn.melodify.domain.model.PlayMode
import com.andannn.melodify.domain.model.PlayerState
import com.andannn.melodify.shared.compose.common.theme.MelodifyTheme
import com.andannn.melodify.shared.compose.common.widgets.LinerWaveSlider
import com.andannn.melodify.shared.compose.common.widgets.MarqueeText
import com.andannn.melodify.shared.compose.components.play.control.PlayerUiEvent
import com.andannn.melodify.ui.LocalSystemUiController
import com.andannn.melodify.ui.player.internal.common.PlayControlButtons
import com.andannn.melodify.ui.player.internal.port.player.MaxImagePaddingStart
import com.andannn.melodify.ui.player.internal.util.formatDuration
import com.andannn.melodify.util.immersive.NoActionSystemUiController
import kotlin.math.roundToLong

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun AVPlayerControlWidget(
    title: String,
    subTitle: String,
    duration: Long,
    progress: Float,
    isShuffle: Boolean,
    playerState: PlayerState,
    playMode: PlayMode,
    modifier: Modifier = Modifier,
    onEvent: (PlayerUiEvent) -> Unit = {},
    onShrink: () -> Unit = {},
    onToggleShowPlayQueue: () -> Unit = {},
) {
    val systemUiController = LocalSystemUiController.current

    DisposableEffect(systemUiController) {
        systemUiController.setSystemUiDarkTheme(true)
        onDispose {
            systemUiController.setSystemUiStyleAuto()
        }
    }

    Box(
        modifier =
            modifier
                .background(Color.Black.copy(alpha = 0.5f))
                .systemBarsPadding(),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
        ) {
            Row(modifier = Modifier.padding(top = 8.dp)) {
                IconButton(
                    modifier = Modifier.rotate(-90f),
                    onClick = onShrink,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBackIos,
                        contentDescription = "Shrink",
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    MarqueeText(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = MaxImagePaddingStart),
                        text = title,
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = MaxImagePaddingStart),
                        text = subTitle,
                        maxLines = 2,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                IconButton(
                    modifier = Modifier,
                    onClick = onToggleShowPlayQueue,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.QueueMusic,
                        contentDescription = "Queue",
                    )
                }
            }
        }
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
            horizontalArrangement = Arrangement.Center,
        ) {
            Spacer(Modifier.weight(1f))
            PlayControlButtons(
                modifier = Modifier.weight(2f),
                isShuffle = isShuffle,
                playerState = playerState,
                playMode = playMode,
                onEvent = onEvent,
            )
            Spacer(Modifier.weight(1f))
        }

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val durationString =
                    remember(duration) {
                        formatDuration(duration)
                    }
                val progressString =
                    remember(progress, duration) {
                        formatDuration((progress * duration).roundToLong())
                    }

                Text(progressString, style = MaterialTheme.typography.labelLarge)
                LinerWaveSlider(
                    value = progress,
                    modifier = Modifier.weight(1f),
                    onValueChange = {
                        onEvent(PlayerUiEvent.OnProgressChange(it))
                    },
                    onValueChangeFinished = { onEvent(PlayerUiEvent.OnStopChangeProgress) },
                    playing = playerState == PlayerState.PLAYING,
                )
                Text(durationString, style = MaterialTheme.typography.labelLarge)
            }
            IconButton(
                onClick = onShrink,
                colors =
                    IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ),
            ) {
                Icon(
                    imageVector = Icons.Default.Fullscreen,
                    contentDescription = "Play",
                )
            }
        }
    }
}

@Preview(widthDp = 2160, heightDp = 1080)
@Composable
private fun AVPlayerControlWidgetPreview() {
    MelodifyTheme {
        Surface {
            CompositionLocalProvider(
                LocalSystemUiController provides NoActionSystemUiController,
            ) {
                AVPlayerControlWidget(
                    title = "title",
                    subTitle = "subtitle",
                    duration = 10000L,
                    progress = 0.5f,
                    isShuffle = false,
                    playerState = PlayerState.PLAYING,
                    playMode = PlayMode.REPEAT_ALL,
                )
            }
        }
    }
}
