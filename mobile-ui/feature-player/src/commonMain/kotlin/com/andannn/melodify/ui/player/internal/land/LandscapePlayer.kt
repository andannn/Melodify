/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.player.internal.land

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationEventHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.andannn.melodify.domain.model.subTitle
import com.andannn.melodify.shared.compose.components.play.control.PlayerUiEvent
import com.andannn.melodify.shared.compose.components.play.control.PlayerUiState
import com.andannn.melodify.ui.LocalScreenOrientationController
import com.andannn.melodify.ui.player.LocalPlayerStateHolder
import com.andannn.melodify.ui.player.internal.land.player.LandScapeExpandedPlayerLayout
import com.andannn.melodify.ui.player.internal.land.player.LandScapeShrinkPlayerLayout

@Composable
internal fun LandscapePlayer(
    modifier: Modifier = Modifier,
    state: PlayerUiState.Active,
    onEvent: (PlayerUiEvent) -> Unit,
    onRequestDarkTheme: (Boolean) -> Unit,
) {
    val screenController = LocalScreenOrientationController.current
    val playerStateHolder = LocalPlayerStateHolder.current
    val initialIsExpand = playerStateHolder.isExpand
    val initialIsQueueOpened = playerStateHolder.isQueueOpened

    var isFullScreen by remember {
        mutableStateOf(initialIsExpand)
    }

    LaunchedEffect(isFullScreen) {
        playerStateHolder.isExpand = isFullScreen
    }

    Box(modifier = modifier) {
        if (isFullScreen) {
            DisposableEffect(Unit) {
                onRequestDarkTheme(true)
                onDispose {
                    onRequestDarkTheme(false)
                }
            }

            fun shrinkLandscapePlayer() {
                if (screenController.isRequestLandscape()) {
                    // Change to portrait screen if back from fullscreen Landscape layout.
                    screenController.cancelRequest()
                } else {
                    isFullScreen = false
                }
            }
            NavigationEventHandler(
                state = rememberNavigationEventState(NavigationEventInfo.None),
                isBackEnabled = true,
            ) {
                shrinkLandscapePlayer()
            }

            LandScapeExpandedPlayerLayout(
                playMode = state.playMode,
                initialIsQueueOpened = initialIsQueueOpened,
                isShuffle = state.isShuffle,
                isPlaying = state.isPlaying,
                title = state.mediaItem.name,
                subTitle = state.mediaItem.subTitle,
                progress = state.progress,
                duration = state.duration,
                onShrink = {
                    shrinkLandscapePlayer()
                },
                onEvent = onEvent,
            )
        } else {
            LandScapeShrinkPlayerLayout(
                title = state.mediaItem.name,
                subTitle = state.mediaItem.subTitle,
                isPlaying = state.isPlaying,
                isFavorite = state.isFavorite,
                onEvent = onEvent,
                onExpand = {
                    isFullScreen = true
                },
            )
        }
    }
}
