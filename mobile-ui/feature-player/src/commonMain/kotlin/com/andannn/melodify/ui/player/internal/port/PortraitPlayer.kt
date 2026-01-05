/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.player.internal.port

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationEventHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.andannn.melodify.domain.model.subTitle
import com.andannn.melodify.shared.compose.components.play.control.PlayerUiEvent
import com.andannn.melodify.shared.compose.components.play.control.PlayerUiState
import com.andannn.melodify.ui.LocalScreenOrientationController
import com.andannn.melodify.ui.player.LocalPlayerStateHolder
import com.andannn.melodify.ui.player.PlayerLayoutState
import com.andannn.melodify.ui.player.PlayerStateHolder
import com.andannn.melodify.ui.player.internal.port.player.BottomSheetState
import com.andannn.melodify.ui.player.internal.port.player.PlayerViewState
import com.andannn.melodify.ui.player.internal.port.player.PortraitPlayerLayout
import com.andannn.melodify.ui.player.internal.port.player.rememberPlayerViewState

@Composable
internal fun PortraitPlayer(
    modifier: Modifier = Modifier,
    initialIsExpand: Boolean,
    state: PlayerUiState.Active,
    onEvent: (PlayerUiEvent) -> Unit,
) {
    val playerStateHolder = LocalPlayerStateHolder.current
    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
    ) {
        val screenController = LocalScreenOrientationController.current
        val layoutState: PlayerViewState =
            rememberPlayerViewState(
                initialPlayerState = if (initialIsExpand) PlayerState.Expand else PlayerState.Shrink,
                initialBottomSheetState = BottomSheetState.Shrink,
                screenSize =
                    Size(
                        width = constraints.maxWidth.toFloat(),
                        height = constraints.maxHeight.toFloat(),
                    ),
                navigationBarHeightPx = WindowInsets.navigationBars.getBottom(LocalDensity.current),
                statusBarHeightPx = WindowInsets.statusBars.getTop(LocalDensity.current),
                density = LocalDensity.current,
            )

        LaunchedEffect(layoutState.playerState) {
            playerStateHolder.onPlayerState(layoutState.playerState)
        }

        NavigationEventHandler(
            state = rememberNavigationEventState(NavigationEventInfo.None),
            isBackEnabled = layoutState.playerState == PlayerState.Expand,
        ) {
            layoutState.shrinkPlayerLayout()
        }

        PortraitPlayerLayout(
            modifier =
                Modifier
                    .height(with(LocalDensity.current) { layoutState.playerExpandState.offset.toDp() })
                    .align(Alignment.BottomCenter)
                    .anchoredDraggable(
                        layoutState.playerExpandState,
                        enabled = !layoutState.isBottomSheetExpanding,
                        orientation = Orientation.Vertical,
                        reverseDirection = true,
                    ).clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        enabled = layoutState.playerState == PlayerState.Shrink,
                        onClick = layoutState::expandPlayerLayout,
                    ),
            layoutState = layoutState,
            playMode = state.playMode,
            isShuffle = state.isShuffle,
            isPlaying = state.isPlaying,
            isFavorite = state.isFavorite,
            activeMediaItem = state.mediaItem,
            isCounting = state.isCounting,
            title = state.mediaItem.name,
            subTitle = state.mediaItem.subTitle,
            progress = state.progress,
            duration = state.duration,
            onShrinkButtonClick = layoutState::shrinkPlayerLayout,
            onEvent = onEvent,
            onRequestFullScreen = {
                screenController.requestLandscape()
            },
        )
    }
}

private fun PlayerStateHolder.onPlayerState(state: PlayerState) {
    onReportPlayState(
        state.toLayoutState(),
    )
}

private fun PlayerState.toLayoutState() =
    when (this) {
        PlayerState.Shrink -> PlayerLayoutState.Shrink
        PlayerState.Expand -> PlayerLayoutState.Expand
    }
