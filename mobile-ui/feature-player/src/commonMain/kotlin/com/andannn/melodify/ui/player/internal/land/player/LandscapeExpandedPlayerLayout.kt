/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.player.internal.land.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationEventHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.andannn.melodify.domain.model.PlayMode
import com.andannn.melodify.shared.compose.components.play.control.PlayerUiEvent
import com.andannn.melodify.ui.ImmersiveModeEffect
import com.andannn.melodify.ui.KeepScreenOnEffect
import com.andannn.melodify.ui.player.LocalPlayerStateHolder
import com.andannn.melodify.ui.player.internal.AVPlayerView
import com.andannn.melodify.ui.player.internal.land.player.cover.AVPlayerControlWidget
import com.andannn.melodify.ui.player.internal.land.player.cover.PlayerGestureFunctionCover
import com.andannn.melodify.ui.player.internal.land.player.queue.QueueWithHeader

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

                QueueWithHeader(
                    onCloseQueue = {
                        isQueueVisible = false
                    },
                )
            }
        }
    }

    ImmersiveModeEffect()
    if (isPlaying) {
        KeepScreenOnEffect()
    }
}
