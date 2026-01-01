/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.player.internal.land.player

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andannn.melodify.shared.compose.components.play.control.PlayerUiEvent
import com.andannn.melodify.ui.player.internal.AVPlayerView
import com.andannn.melodify.ui.player.internal.common.MiniPlayerLayout

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun LandScapeShrinkPlayerLayout(
    title: String,
    subTitle: String,
    isPlaying: Boolean,
    isFavorite: Boolean,
    modifier: Modifier = Modifier,
    onEvent: (PlayerUiEvent) -> Unit = {},
    onExpand: () -> Unit = {},
) {
    Box(
        modifier =
            modifier
                .fillMaxSize(),
    ) {
        Surface(
            modifier =
                Modifier
                    .fillMaxWidth(2 / 3f)
                    .align(Alignment.BottomCenter)
                    .clickable(
                        indication = null,
                        interactionSource = null,
                        onClick = onExpand,
                    ),
            shape =
                RoundedCornerShape(
                    topStart = 8.dp,
                    topEnd = 8.dp,
                ),
            shadowElevation = 24.dp,
        ) {
            Column(
                modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp),
            ) {
                Row {
                    AVPlayerView(
                        modifier = Modifier.size(60.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    MiniPlayerLayout(
                        modifier = Modifier.weight(1f),
                        title = title,
                        subTitle = subTitle,
                        isPlaying = isPlaying,
                        isFavorite = isFavorite,
                        onEvent = onEvent,
                    )
                }
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            }
        }
    }
}
