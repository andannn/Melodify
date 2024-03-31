package com.andanana.musicplayer.feature.player.widget

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import com.andanana.musicplayer.core.designsystem.component.MusicCard
import com.andanana.musicplayer.feature.player.PlayerUiEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BottomPlayQueueSheet(
    sheetMaxHeightDp: Dp,
    state: AnchoredDraggableState<BottomSheetState>,
    playListQueue: List<MediaItem>,
    activeMediaItem: MediaItem,
    modifier: Modifier = Modifier,
    scope: CoroutineScope = rememberCoroutineScope(),
    onEvent: (PlayerUiEvent) -> Unit = {},
) {
    val shrinkOffset = state.anchors.positionOf(BottomSheetState.Shrink)
    val expandOffset = state.anchors.positionOf(BottomSheetState.Expand)

    // shrink is 0f, expand is 1f
    val expandFactor by remember {
        derivedStateOf {
            1 - state.offset.div(shrinkOffset - expandOffset)
        }
    }

    val isExpand by remember {
        derivedStateOf {
            state.currentValue == BottomSheetState.Expand
        }
    }

    BackHandler(enabled = isExpand) {
        scope.launch {
            state.animateTo(BottomSheetState.Shrink)
        }
    }

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(sheetMaxHeightDp)
                .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                .offset {
                    IntOffset(
                        0,
                        state
                            .requireOffset()
                            .roundToInt(),
                    )
                },
    ) {
        Box {
            Column(
                modifier =
                    Modifier
                        .graphicsLayer { alpha = expandFactor }
                        .background(MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Box(
                    modifier = Modifier.height(BottomSheetDragAreaHeight),
                )

                HorizontalDivider()

                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f),
                ) {
                    LazyColumn {
                        items(
                            items = playListQueue,
                            key = { it.mediaId },
                        ) { item ->
                            MusicCard(
                                modifier =
                                    Modifier
                                        .padding(vertical = 4.dp)
                                        .animateItemPlacement(),
                                isActive = item.mediaId == activeMediaItem.mediaId,
                                albumArtUri = item.mediaMetadata.artworkUri.toString(),
                                title = item.mediaMetadata.title.toString(),
                                showTrackNum = false,
                                artist = item.mediaMetadata.artist.toString(),
                                trackNum = item.mediaMetadata.trackNumber ?: 0,
                                onMusicItemClick = {
                                },
                                onOptionButtonClick = {
                                },
                            )
                        }
                    }
                }
            }

            Box(
                modifier =
                    Modifier
                        .align(Alignment.TopCenter)
                        .height(BottomSheetDragAreaHeight)
                        .anchoredDraggable(state, orientation = Orientation.Vertical)
                        .fillMaxWidth(),
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "UP NEXT",
                )
            }
        }
    }
}

@ExperimentalFoundationApi
@Preview
@Composable
private fun BottomPlayQueueSheetPreview() {
    val density = LocalDensity.current
    val anchors =
        with(LocalDensity.current) {
            DraggableAnchors {
                BottomSheetState.Shrink at 120.dp.toPx()
                BottomSheetState.Expand at 0f
            }
        }

    val state =
        remember {
            AnchoredDraggableState(
                initialValue = BottomSheetState.Expand,
                anchors = anchors,
                positionalThreshold = { with(density) { 26.dp.toPx() } },
                velocityThreshold = { with(density) { 20.dp.toPx() } },
                animationSpec = spring(),
            )
        }

    BottomPlayQueueSheet(
        sheetMaxHeightDp = 360.dp,
        state = state,
        playListQueue = emptyList(),
        activeMediaItem = MediaItem.EMPTY,
    )
}
