/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.playcontrol.ui.shrinkable

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.PlayMode
import com.andannn.melodify.ui.components.playcontrol.PlayerUiEvent
import com.andannn.melodify.ui.components.playcontrol.ui.MinImageSize
import com.andannn.melodify.ui.components.playcontrol.ui.PlayerViewState
import com.andannn.melodify.ui.components.playcontrol.ui.shrinkable.bottom.PlayerBottomSheetView
import com.andannn.melodify.ui.widgets.CircleBorderImage
import com.andannn.melodify.ui.widgets.ProgressIndicator

val MinImagePaddingTop = 5.dp

val MinImagePaddingStart = 5.dp
val MaxImagePaddingStart = 20.dp

val MinFadeoutWithExpandAreaPaddingTop = 15.dp

val BottomSheetDragAreaHeight = 110.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun FlexiblePlayerLayout(
    layoutState: PlayerViewState,
    coverUri: String,
    activeMediaItem: AudioItemModel,
    modifier: Modifier = Modifier,
    playMode: PlayMode = PlayMode.REPEAT_ALL,
    isShuffle: Boolean = false,
    isPlaying: Boolean = false,
    isFavorite: Boolean = false,
    isCounting: Boolean = false,
    title: String = "",
    artist: String = "",
    progress: Float = 1f,
    onEvent: (PlayerUiEvent) -> Unit = {},
    onShrinkButtonClick: () -> Unit = {},
) {
    val statusBarHeight =
        with(LocalDensity.current) {
            WindowInsets.statusBars.getTop(this).toDp()
        }
    val coverUriState = rememberUpdatedState(newValue = coverUri)

    Surface(
        modifier =
            modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp),
        shadowElevation = 10.dp,
    ) {
        Box(
            modifier =
                Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background),
        ) {
            val fadeInAreaAlpha by remember {
                derivedStateOf {
                    (1f - (1f - layoutState.imageTransactionFactor).times(3f)).coerceIn(0f, 1f)
                }
            }
            val fadeoutAreaAlpha by remember {
                derivedStateOf {
                    1 - (layoutState.imageTransactionFactor * 4).coerceIn(0f, 1f)
                }
            }
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
                artist = artist,
                isPlaying = isPlaying,
                isFavorite = isFavorite,
                onEvent = onEvent,
            )

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

            CircleBorderImage(
                modifier =
                    Modifier
                        .padding(
                            top = layoutState.imagePaddingTopDp,
                            start = layoutState.imagePaddingStartDp,
                        ).width(layoutState.imageSizeDp)
                        .aspectRatio(1f),
                model = coverUriState.value,
            )

            Column(
                modifier =
                    Modifier.fillMaxSize(),
            ) {
                Spacer(modifier = Modifier.height(layoutState.imagePaddingTopDp + layoutState.imageSizeDp))

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
                    title = title,
                    artist = artist,
                    onEvent = onEvent,
                )
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
                ProgressIndicator(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = with(LocalDensity.current) { layoutState.navigationBarHeightPx.toDp() })
                            .align(BottomStart),
                    progress = progress,
                    playing = isPlaying,
                )
            }
        }
    }
}
