/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.player.internal.shrinkable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.andannn.melodify.domain.model.PlayMode
import com.andannn.melodify.shared.compose.components.play.control.PlayerUiEvent
import com.andannn.melodify.ui.KeepScreenOnEffect
import com.andannn.melodify.ui.player.internal.AVPlayerView
import com.andannn.melodify.ui.player.internal.cover.AVPlayerControlWidget
import com.andannn.melodify.ui.player.internal.cover.PlayerGestureFunctionCover
import com.andannn.melodify.util.immersive.ImmersiveModeEffect

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun LandScapeExpandedPlayerLayout(
    modifier: Modifier = Modifier,
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
    Surface(modifier = modifier.fillMaxSize()) {
        AVPlayerView(
            modifier = Modifier.fillMaxSize(),
        )
        PlayerGestureFunctionCover(
            onEvent = onEvent,
        ) {
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
            )
        }
    }

    ImmersiveModeEffect()
    if (isPlaying) {
        KeepScreenOnEffect()
    }
}
