/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.player.internal.land.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationEventHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.andannn.melodify.domain.model.MediaItemModel
import com.andannn.melodify.domain.model.PlayMode
import com.andannn.melodify.shared.compose.components.play.control.PlayerUiEvent
import com.andannn.melodify.shared.compose.components.queue.PlayQueue
import com.andannn.melodify.ui.ImmersiveModeEffect
import com.andannn.melodify.ui.KeepScreenOnEffect
import com.andannn.melodify.ui.player.LocalPlayerStateHolder
import com.andannn.melodify.ui.player.internal.AVPlayerView
import com.andannn.melodify.ui.player.internal.land.player.cover.AVPlayerControlWidget
import com.andannn.melodify.ui.player.internal.land.player.cover.PlayerGestureFunctionCover
import melodify.shared.compose.resource.generated.resources.Res
import melodify.shared.compose.resource.generated.resources.play_queue
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun LandScapeExpandedPlayerLayout(
    modifier: Modifier = Modifier,
    initialIsQueueOpened: Boolean,
    playMode: PlayMode = PlayMode.REPEAT_ALL,
    isShuffle: Boolean = false,
    isPlaying: Boolean = false,
    title: String = "",
    subTitle: String = "",
    progress: Float = 1f,
    duration: Long = 0L,
    onEvent: (PlayerUiEvent) -> Unit = {},
    onShrink: () -> Unit = {},
) {
    var isQueueVisible by remember {
        mutableStateOf(initialIsQueueOpened)
    }
    val playerLayoutStateHolder = LocalPlayerStateHolder.current
    LaunchedEffect(isQueueVisible) {
        playerLayoutStateHolder.isQueueOpened = isQueueVisible
    }

    Surface(modifier = modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                AVPlayerView()
                PlayerGestureFunctionCover(
                    onEvent = onEvent,
                    controlWidget = {
                        AVPlayerControlWidget(
                            title = title,
                            subTitle = subTitle,
                            duration = duration,
                            isShuffle = isShuffle,
                            playMode = playMode,
                            isPlaying = isPlaying,
                            progress = progress,
                            onEvent = onEvent,
                            onShrink = onShrink,
                            onToggleShowPlayQueue = {
                                isQueueVisible = !isQueueVisible
                            },
                        )
                    },
                )
            }

            AnimatedVisibility(
                visible = isQueueVisible,
                enter = expandHorizontally(),
                exit = shrinkHorizontally(),
            ) {
                NavigationEventHandler(
                    state = rememberNavigationEventState(NavigationEventInfo.None),
                    isBackEnabled = true,
                ) {
                    isQueueVisible = false
                }

                Column(
                    modifier = Modifier.fillMaxHeight().width(360.dp),
                ) {
                    Row(
                        modifier =
                            Modifier
                                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                                .padding(vertical = 8.dp)
                                .statusBarsPadding(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = stringResource(Res.string.play_queue),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Spacer(Modifier.weight(1f))
                        IconButton(
                            onClick = {
                                isQueueVisible = false
                            },
                        ) {
                            Icon(
                                Icons.Rounded.Close,
                                contentDescription = null,
                            )
                        }
                    }
                    PlayQueue()
                }
            }
        }
    }

    ImmersiveModeEffect()
    if (isPlaying) {
        KeepScreenOnEffect()
    }
}
