/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.player.internal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andannn.melodify.domain.Repository
import com.andannn.melodify.shared.compose.common.widgets.MediaCoverImageWidget
import org.koin.mp.KoinPlatform.getKoin

@androidx.compose.runtime.Composable
internal actual fun AVPlayerView(modifier: Modifier) {
    val repository = getKoin().get<Repository>()
    val playingMedia by repository
        .getPlayingMediaStateFlow()
        .collectAsStateWithLifecycle(null)
    val artworkUri = playingMedia?.artWorkUri

    Box(modifier = modifier.clip(shape = RoundedCornerShape(8.dp))) {
        if (artworkUri != null) {
            MediaCoverImageWidget(modifier = Modifier.fillMaxSize(), model = artworkUri)
        }
    }
}
