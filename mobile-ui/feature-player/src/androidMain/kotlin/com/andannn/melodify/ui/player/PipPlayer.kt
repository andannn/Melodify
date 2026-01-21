/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.player

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.andannn.melodify.ui.player.internal.AVPlayerView

@Composable
fun PipPlayer(modifier: Modifier) {
    AVPlayerView(
        modifier = modifier,
    )
}
