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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import com.andannn.melodify.ui.common.dynamictheming.DynamicThemePrimaryColorsFromImage
import com.andannn.melodify.ui.common.dynamictheming.rememberDominantColorState
import com.andannn.melodify.ui.common.theme.MIN_CONTRAST_OF_PRIMARY_VS_SURFACE
import com.andannn.melodify.ui.common.util.contrastAgainst
import com.andannn.melodify.ui.common.widgets.AndroidBackHandler
import com.andannn.melodify.ui.components.playcontrol.PlayerUiEvent
import com.andannn.melodify.ui.components.playcontrol.PlayerUiState
import com.andannn.melodify.ui.components.playcontrol.ui.shrinkable.FlexiblePlayerLayout

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun PlayerViewContent(
    state: PlayerUiState.Active,
    modifier: Modifier = Modifier,
    onEvent: (PlayerUiEvent) -> Unit,
) {
    val navigationBarHeight = WindowInsets.navigationBars.getBottom(LocalDensity.current)
    val statusBarHeight = WindowInsets.statusBars.getTop(LocalDensity.current)
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
    ) {
        val layoutState: PlayerViewState =
            rememberPlayerViewState(
                screenSize =
                    Size(
                        width = constraints.maxWidth.toFloat(),
                        height = constraints.maxHeight.toFloat(),
                    ),
                navigationBarHeightPx = navigationBarHeight,
                statusBarHeightPx = statusBarHeight,
                density = density,
            )

        val isPlayerDraggable by
            remember {
                derivedStateOf {
                    !layoutState.isBottomSheetExpanding
                }
            }

        AndroidBackHandler(
            enabled = layoutState.playerState == PlayerState.Expand,
            layoutState::shrinkPlayerLayout,
        )

        val surfaceColor = MaterialTheme.colorScheme.surface
        val dominantColorState =
            rememberDominantColorState { color ->
                // We want a color which has sufficient contrast against the surface color
                color.contrastAgainst(surfaceColor) >= MIN_CONTRAST_OF_PRIMARY_VS_SURFACE
            }
        DynamicThemePrimaryColorsFromImage(dominantColorState) {
            val url = state.mediaItem.artWorkUri
            // When the selected image url changes, call updateColorsFromImageUrl() or reset()
            LaunchedEffect(url) {
                dominantColorState.updateColorsFromImageUrl(url)
            }

            LaunchedEffect(layoutState.isPlayerExpanding) {
                dominantColorState.setDynamicThemeEnable(layoutState.isPlayerExpanding)
            }

            FlexiblePlayerLayout(
                modifier =
                    Modifier
                        .height(with(LocalDensity.current) { layoutState.playerExpandState.offset.toDp() })
                        .align(Alignment.BottomCenter)
                        .anchoredDraggable(
                            layoutState.playerExpandState,
                            enabled = isPlayerDraggable,
                            orientation = Orientation.Vertical,
                            reverseDirection = true,
                        ).clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            enabled = layoutState.playerState == PlayerState.Shrink,
                            onClick = layoutState::expandPlayerLayout,
                        ),
                layoutState = layoutState,
                coverUri = state.mediaItem.artWorkUri,
                playMode = state.playMode,
                isShuffle = state.isShuffle,
                isPlaying = state.isPlaying,
                isFavorite = state.isFavorite,
                activeMediaItem = state.mediaItem,
                isCounting = state.isCounting,
                title = state.mediaItem.name,
                artist = state.mediaItem.artist,
                progress = state.progress,
                onShrinkButtonClick = layoutState::shrinkPlayerLayout,
                onEvent = onEvent,
            )
        }
    }
}
