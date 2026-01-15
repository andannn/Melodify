/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.player.internal.port.player.bottom

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationEventHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.andannn.melodify.shared.compose.common.theme.MelodifyTheme
import com.andannn.melodify.shared.compose.components.lyrics.Lyrics
import com.andannn.melodify.shared.compose.components.queue.PlayQueue
import com.andannn.melodify.ui.player.LocalPlayerStateHolder
import com.andannn.melodify.ui.player.internal.port.player.BottomSheetState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
internal fun PlayerBottomSheetView(
    state: AnchoredDraggableState<BottomSheetState>,
    initialSelectedTab: SheetTab?,
    modifier: Modifier = Modifier,
    scope: CoroutineScope = rememberCoroutineScope(),
    onRequestExpandSheet: () -> Unit = {},
) {
    val isExpand by remember {
        derivedStateOf {
            state.currentValue == BottomSheetState.Expand
        }
    }

    val sheetState = rememberPlayerBottomSheetState(initialSelectedTab)
    val layoutStateHolder = LocalPlayerStateHolder.current
    LaunchedEffect(sheetState.selectedTab, state.currentValue) {
        if (sheetState.selectedTab == SheetTab.NEXT_SONG && state.currentValue == BottomSheetState.Expand) {
            layoutStateHolder.isQueueOpened = true
        } else {
            layoutStateHolder.isQueueOpened = false
        }
    }
    NavigationEventHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = isExpand,
    ) {
        scope.launch {
            state.animateTo(BottomSheetState.Shrink)
        }
    }

    BottomSheetContainer(
        modifier =
            modifier
                .fillMaxSize()
                .offset {
                    IntOffset(
                        0,
                        state
                            .requireOffset()
                            .roundToInt(),
                    )
                },
        sheetState = sheetState,
        state = state,
        onRequestExpandSheet = onRequestExpandSheet,
    ) { selectedTab ->
        when (selectedTab) {
            SheetTab.NEXT_SONG -> {
                PlayQueue()
            }

            SheetTab.LYRICS -> {
                Lyrics()
            }
        }
    }
}

@Composable
private fun BottomSheetContainer(
    sheetState: PlayerBottomSheetState,
    state: AnchoredDraggableState<BottomSheetState>,
    modifier: Modifier = Modifier,
    onRequestExpandSheet: () -> Unit = {},
    content: @Composable (selectedSheet: SheetTab) -> Unit = {},
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

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = expandFactor),
        shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            CustomTabBar(
                modifier =
                    Modifier
                        .padding(horizontal = 16.dp)
                        .anchoredDraggable(state, orientation = Orientation.Vertical),
                showIndicator = isExpand,
                items = sheetState.sheetItems.toImmutableList(),
                selectedTabIndex = sheetState.selectedIndex,
                onItemPressed = {
                    sheetState.onSelectItem(it)
                },
                onItemClick = {
                    sheetState.onSelectItem(it)
                    onRequestExpandSheet()
                },
            )
            Spacer(modifier = Modifier.height(3.dp))

            if (expandFactor != 0f) {
                Surface(
                    modifier =
                        Modifier
                            .weight(1f)
                            .graphicsLayer { alpha = expandFactor }
                            .padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
                ) {
                    content(sheetState.selectedTab)
                }
            }
        }
    }
}

@Preview
@Composable
private fun BottomSheetContainerPreview() {
    MelodifyTheme {
        Surface {
            BottomSheetContainer(
                sheetState = rememberPlayerBottomSheetState(null),
                state =
                    AnchoredDraggableState(
                        initialValue = BottomSheetState.Expand,
                        anchors =
                            DraggableAnchors {
                                BottomSheetState.Shrink at -100f
                                BottomSheetState.Expand at 0f
                            },
                    ),
            ) {
                Spacer(modifier = Modifier.fillMaxSize().background(Color.Red))
            }
        }
    }
}
