/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.player.internal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.modifiers.resizeWithContentScale
import androidx.media3.ui.compose.state.rememberPresentationState
import com.andannn.melodify.core.player.MediaBrowserManager
import com.andannn.melodify.domain.PlayerStateMonitoryRepository
import com.andannn.melodify.domain.model.AudioItemModel
import com.andannn.melodify.domain.model.VideoItemModel
import com.andannn.melodify.shared.compose.common.LocalRepository
import com.andannn.melodify.shared.compose.common.widgets.MediaCoverImageWidget
import org.koin.mp.KoinPlatform.getKoin

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
internal actual fun AVPlayerView(modifier: Modifier) {
    val playerStateMonitoryRepository: PlayerStateMonitoryRepository = LocalRepository.current
    val mediaBrowserManager: MediaBrowserManager = getKoin().get()
    val playingMedia by playerStateMonitoryRepository
        .getPlayingMediaStateFlow()
        .collectAsStateWithLifecycle(null)
    Box(
        modifier = modifier,
    ) {
        when (playingMedia) {
            is AudioItemModel -> {
                MediaCoverImageWidget(
                    modifier = Modifier.fillMaxSize(),
                    model = playingMedia?.artWorkUri ?: "",
                )
            }

            is VideoItemModel -> {
                val player =
                    remember {
                        mediaBrowserManager.mediaBrowser
                    }
                val presentationState = rememberPresentationState(player)
                val scaledModifier =
                    Modifier.resizeWithContentScale(ContentScale.Fit, presentationState.videoSizeDp)
                PlayerSurface(
                    modifier = scaledModifier,
                    player = player,
                )
            }

            else -> {}
        }
    }
}
