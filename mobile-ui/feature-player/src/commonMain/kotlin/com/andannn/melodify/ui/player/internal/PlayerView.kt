/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.player.internal

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import com.andannn.melodify.shared.compose.components.play.control.PlayerUiEvent
import com.andannn.melodify.shared.compose.components.play.control.PlayerUiState
import com.andannn.melodify.ui.LocalScreenOrientationController
import com.andannn.melodify.ui.player.LocalPlayerStateHolder
import com.andannn.melodify.ui.player.PlayerLayoutState
import com.andannn.melodify.ui.player.internal.land.LandscapePlayer
import com.andannn.melodify.ui.player.internal.port.PortraitPlayer
import com.andannn.melodify.ui.player.internal.theme.DynamicThemePrimaryColorsFromImage
import com.andannn.melodify.ui.player.internal.theme.rememberDominantColorState
import com.andannn.melodify.ui.player.internal.util.contrastAgainst

/**
 * This is the minimum amount of calculated contrast for a color to be used on top of the
 * surface color. These values are defined within the WCAG AA guidelines, and we use a value of
 * 3:1 which is the minimum for user-interface components.
 */
const val MIN_CONTRAST_OF_PRIMARY_VS_SURFACE = 3f

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
internal fun PlayerView(
    state: PlayerUiState.Active,
    modifier: Modifier = Modifier,
    onEvent: (PlayerUiEvent) -> Unit,
) {
    val playerStateHolder = LocalPlayerStateHolder.current
    val screenController = LocalScreenOrientationController.current

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
    Box(
        modifier = modifier,
    ) {
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

            if (screenController.isCurrentPortrait) {
                PortraitPlayer(
                    initialIsExpand = playerStateHolder.playerLayoutState == PlayerLayoutState.Expand,
                    state = state,
                    onEvent = onEvent,
                )
            } else {
                LandscapePlayer(
                    initialIsFullScreen = playerStateHolder.playerLayoutState == PlayerLayoutState.Expand,
                    state = state,
                    onEvent = onEvent,
                    onRequestDarkTheme = {
                        isRequestDarkTheme = it
                    },
                    onReportPlayerShink = {
                        playerStateHolder.onReportPlayState(PlayerLayoutState.Shrink)
                    },
                    onReportPlayerExpand = {
                        playerStateHolder.onReportPlayState(PlayerLayoutState.Expand)
                    },
                )
            }
        }
    }
}
