/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.playcontrol.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationEventHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.andannn.melodify.LocalScreenController
import com.andannn.melodify.core.data.model.subTitle
import com.andannn.melodify.ui.components.playcontrol.PlayerUiEvent
import com.andannn.melodify.ui.components.playcontrol.PlayerUiState
import com.andannn.melodify.ui.components.playcontrol.ui.shrinkable.LandScapeExpandedPlayerLayout
import com.andannn.melodify.ui.components.playcontrol.ui.shrinkable.LandScapeShrinkPlayerLayout
import com.andannn.melodify.ui.components.playcontrol.ui.shrinkable.PortraitPlayerLayout
import com.andannn.melodify.ui.theme.DynamicThemePrimaryColorsFromImage
import com.andannn.melodify.ui.theme.MIN_CONTRAST_OF_PRIMARY_VS_SURFACE
import com.andannn.melodify.ui.theme.rememberDominantColorState
import com.andannn.melodify.ui.util.contrastAgainst

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
internal fun PlayerViewContent(
    state: PlayerUiState.Active,
    modifier: Modifier = Modifier,
    onEvent: (PlayerUiEvent) -> Unit,
) {
    val screenController = LocalScreenController.current
    var retainedPlayerState by
        retain {
            mutableStateOf(PlayerState.Shrink)
        }
    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
    ) {
        val layoutState: PlayerViewState =
            rememberPlayerViewState(
                initialPlayerState = retainedPlayerState,
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

        fun shrinkWhenLandscape() {
            if (screenController.isRequestLandscape()) {
                // Change to portrait screen if back from fullscreen Landscape layout.
                screenController.cancelRequest()
            } else {
                layoutState.shrinkPlayerLayout()
            }
        }

        LaunchedEffect(layoutState.playerState) {
            retainedPlayerState = layoutState.playerState
        }

        NavigationEventHandler(
            state = rememberNavigationEventState(NavigationEventInfo.None),
            isBackEnabled = layoutState.playerState == PlayerState.Expand,
        ) {
            // onBack
            if (retainedPlayerState == PlayerState.Expand) {
                if (screenController.isCurrentPortrait) {
                    layoutState.shrinkPlayerLayout()
                } else {
                    shrinkWhenLandscape()
                }
            }
        }

        val surfaceColor = MaterialTheme.colorScheme.surface
        val dominantColorState =
            rememberDominantColorState { color ->
                // We want a color which has sufficient contrast against the surface color
                color.contrastAgainst(surfaceColor) >= MIN_CONTRAST_OF_PRIMARY_VS_SURFACE
            }
        val isSystemDarkTheme = isSystemInDarkTheme()
        var isRequestDarkTheme by remember {
            mutableStateOf(false)
        }
        DynamicThemePrimaryColorsFromImage(
            dominantColorState = dominantColorState,
            isDarkTheme = isRequestDarkTheme || isSystemDarkTheme,
        ) {
            val url = state.mediaItem.artWorkUri
            // When the selected image url changes, call updateColorsFromImageUrl() or reset()
            LaunchedEffect(url) {
                if (url != null) {
                    dominantColorState.updateColorsFromImageUrl(url)
                }
            }

            LaunchedEffect(layoutState.isPlayerExpanding) {
                dominantColorState.setDynamicThemeEnable(true)
            }

            if (screenController.isCurrentPortrait) {
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
            } else {
                if (layoutState.playerState == PlayerState.Expand) {
                    DisposableEffect(Unit) {
                        isRequestDarkTheme = true
                        onDispose {
                            isRequestDarkTheme = false
                        }
                    }
                    LandScapeExpandedPlayerLayout(
                        playMode = state.playMode,
                        isShuffle = state.isShuffle,
                        isPlaying = state.isPlaying,
                        isFavorite = state.isFavorite,
                        isCounting = state.isCounting,
                        title = state.mediaItem.name,
                        subTitle = state.mediaItem.subTitle,
                        progress = state.progress,
                        duration = state.duration,
                        onShrink = {
                            shrinkWhenLandscape()
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
                            layoutState.expandPlayerLayout()
                        },
                    )
                }
            }
        }
    }
}
