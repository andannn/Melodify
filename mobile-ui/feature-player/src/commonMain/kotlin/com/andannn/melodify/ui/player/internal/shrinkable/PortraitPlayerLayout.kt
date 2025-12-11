/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.player.internal.shrinkable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.PlayMode
import com.andannn.melodify.shared.compose.components.play.control.PlayerUiEvent
import com.andannn.melodify.ui.player.internal.AVPlayerView
import com.andannn.melodify.ui.player.internal.MinImageSize
import com.andannn.melodify.ui.player.internal.PlayerViewState
import com.andannn.melodify.ui.player.internal.shrinkable.bottom.PlayerBottomSheetView

internal val MinImagePaddingTop = 5.dp

internal val MinImagePaddingStart = 5.dp
internal val MaxImagePaddingStart = 20.dp

internal val MinFadeoutWithExpandAreaPaddingTop = 15.dp

internal val BottomSheetDragAreaHeight = 110.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun PortraitPlayerLayout(
    layoutState: PlayerViewState,
    activeMediaItem: MediaItemModel,
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
    onShrinkButtonClick: () -> Unit = {},
    onRequestFullScreen: () -> Unit = {},
) {
    val statusBarHeight =
        with(LocalDensity.current) {
            WindowInsets.statusBars.getTop(this).toDp()
        }

    Surface(
        modifier =
            modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp),
        shadowElevation = 10.dp,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background),
        ) {
            val fadeInAreaAlpha =
                remember(layoutState.imageTransactionFactor) {
                    (1f - (1f - layoutState.imageTransactionFactor).times(3f)).coerceIn(0f, 1f)
                }
            val fadeoutAreaAlpha =
                remember(layoutState.imageTransactionFactor) {
                    1 - (layoutState.imageTransactionFactor * 4).coerceIn(0f, 1f)
                }
            if (fadeoutAreaAlpha > 0) {
                MiniPlayerLayout(
                    modifier =
                        Modifier
                            .graphicsLayer {
                                alpha = fadeoutAreaAlpha
                            }.fillMaxWidth()
                            .padding(
                                top = layoutState.miniPlayerPaddingTopDp,
                                start = MinImagePaddingStart * 2 + MinImageSize,
                            ),
                    title = title,
                    subTitle = subTitle,
                    isPlaying = isPlaying,
                    isFavorite = isFavorite,
                    onEvent = onEvent,
                )
            }

            if (fadeInAreaAlpha != 0f) {
                PlayerHeader(
                    modifier =
                        Modifier
                            .padding(top = statusBarHeight)
                            .graphicsLayer {
                                alpha = fadeInAreaAlpha
                            },
                    showTimerIcon = isCounting,
                    onShrinkButtonClick = onShrinkButtonClick,
                    onTimerIconClick = {
                        onEvent(PlayerUiEvent.OnTimerIconClick)
                    },
                    onOptionIconClick = {
                        onEvent(PlayerUiEvent.OnOptionIconClick(activeMediaItem))
                    },
                )
            }

            Box(
                modifier =
                    Modifier
                        .padding(
                            top = layoutState.imagePaddingTopDp,
                            start = layoutState.imagePaddingStartDp,
                        ).width(layoutState.imageSizeDp)
                        .aspectRatio(1f),
            ) {
                AVPlayerView()
                if (layoutState.isFullExpanded && !layoutState.isBottomSheetExpanding) {
                    TouchToggleVisible(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        FullScreenButtonCover(
                            onClick = onRequestFullScreen,
                        )
                    }
                }
            }

            Column(
                modifier =
                    Modifier.fillMaxSize(),
            ) {
                Spacer(modifier = Modifier.height(layoutState.imagePaddingTopDp + layoutState.imageSizeDp))

                if (fadeInAreaAlpha > 0) {
                    LargePlayerControlArea(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .graphicsLayer {
                                    alpha = fadeInAreaAlpha
                                },
                        isPlaying = isPlaying,
                        playMode = playMode,
                        isShuffle = isShuffle,
                        progress = progress,
                        duration = duration,
                        title = title,
                        artist = subTitle,
                        onEvent = onEvent,
                    )
                }
                Spacer(modifier = Modifier.height(BottomSheetDragAreaHeight))
            }

            AnimatedVisibility(
                modifier =
                    Modifier.align(Alignment.BottomCenter),
                enter = fadeIn(),
                exit = fadeOut(),
                visible = layoutState.isFullExpanded,
            ) {
                PlayerBottomSheetView(
                    modifier = Modifier.height(with(LocalDensity.current) { layoutState.bottomSheetHeight.toDp() }),
                    state = layoutState.bottomSheetState,
                    onRequestExpandSheet = {
                        layoutState.expandBottomSheet()
                    },
                )
            }

            if (!layoutState.isPlayerExpanding) {
                LinearProgressIndicator(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = with(LocalDensity.current) { layoutState.navigationBarHeightPx.toDp() })
                            .align(BottomStart),
                    progress = { progress },
                )
            }
        }
    }
}
