/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.playcontrol.ui.shrinkable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.andannn.melodify.ImmersiveModeEffect
import com.andannn.melodify.core.data.model.PlayMode
import com.andannn.melodify.ui.components.playcontrol.PlayerUiEvent
import com.andannn.melodify.ui.widgets.AVPlayerView

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun LandScapePlayerLayout(
    modifier: Modifier = Modifier,
    playMode: PlayMode = PlayMode.REPEAT_ALL,
    isShuffle: Boolean = false,
    isPlaying: Boolean = false,
    isFavorite: Boolean = false,
    isCounting: Boolean = false,
    title: String = "",
    subTitle: String = "",
    progress: Float = 1f,
    duration: Long = 0L,
    onEvent: (PlayerUiEvent) -> Unit = {},
    onShrink: () -> Unit = {},
) {
    Surface(modifier = modifier.fillMaxSize()) {
        AVPlayerView(
            modifier = Modifier.fillMaxSize(),
        )
        TouchToggleVisible {
            AVPlayerCover(
                title = title,
                subTitle = subTitle,
                duration = duration,
                isShuffle = isShuffle,
                playMode = playMode,
                isPlaying = isPlaying,
                progress = progress,
                onEvent = onEvent,
                onShrink = onShrink,
            )
        }
    }

    ImmersiveModeEffect()
}
