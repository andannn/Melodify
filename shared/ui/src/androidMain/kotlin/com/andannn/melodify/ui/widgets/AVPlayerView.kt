/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.widgets

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.modifiers.resizeWithContentScale
import androidx.media3.ui.compose.state.rememberPresentationState
import com.andannn.melodify.core.data.MediaControllerRepository
import com.andannn.melodify.core.data.PlayerStateMonitoryRepository
import com.andannn.melodify.core.data.internal.MediaControllerRepositoryImpl
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.VideoItemModel
import com.andannn.melodify.ui.core.LocalRepository

@OptIn(UnstableApi::class)
@Composable
fun AVPlayerView(
    modifier: Modifier = Modifier,
    playerController: MediaControllerRepository = LocalRepository.current.mediaControllerRepository,
    playerStateMonitoryRepository: PlayerStateMonitoryRepository = LocalRepository.current.playerStateMonitoryRepository,
) {
    val playingMedia by playerStateMonitoryRepository
        .getPlayingMediaStateFlow()
        .collectAsStateWithLifecycle(null)
    Box(
        modifier = modifier,
    ) {
        when (playingMedia) {
            is AudioItemModel ->
                CircleBorderImage(
                    modifier = Modifier.fillMaxSize(),
                    model = playingMedia?.artWorkUri ?: "",
                    contentScale = ContentScale.Crop,
                )
            is VideoItemModel -> {
                val player =
                    remember {
                        (playerController as MediaControllerRepositoryImpl).getPlayer()
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
