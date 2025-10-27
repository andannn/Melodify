/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.playcontrol.ui.shrinkable.bottom

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.andannn.melodify.ui.components.lyrics.Lyrics
import com.andannn.melodify.ui.components.playcontrol.ui.BottomSheetState
import com.andannn.melodify.ui.components.queue.PlayQueue
import com.andannn.melodify.ui.widgets.AndroidBackHandler
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import melodify.composeapp.generated.resources.Res
import melodify.composeapp.generated.resources.lyrics
import melodify.composeapp.generated.resources.play_queue
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

private const val TAG = "PlayQueueView"

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun PlayerBottomSheetView(
    state: AnchoredDraggableState<BottomSheetState>,
    modifier: Modifier = Modifier,
    scope: CoroutineScope = rememberCoroutineScope(),
    onRequestExpandSheet: () -> Unit = {},
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

    val sheetState = rememberPlayerBottomSheetState()
    AndroidBackHandler(enabled = isExpand) {
        scope.launch {
            state.animateTo(BottomSheetState.Shrink)
        }
    }

    Surface(
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
        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = expandFactor),
        shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            TabBar(
                modifier =
                    Modifier
                        .padding(horizontal = 16.dp)
                        .anchoredDraggable(state, orientation = Orientation.Vertical),
                isExpand = isExpand,
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
                    when (sheetState.selectedTab) {
                        SheetTab.NEXT_SONG -> {
                            PlayQueue()
                        }

                        SheetTab.LYRICS -> {
                            Lyrics()
                        }
                    }
                }
            }
        }
    }
}

private val emptyIndicator = @Composable { _: List<TabPosition> -> }
private val defaultDivider: @Composable () -> Unit = @Composable { HorizontalDivider() }
private val emptyDivider: @Composable () -> Unit = @Composable { }

@Composable
private fun TabBar(
    isExpand: Boolean,
    items: ImmutableList<SheetTab>,
    selectedTabIndex: Int,
    onItemPressed: (SheetTab) -> Unit,
    modifier: Modifier = Modifier,
    onItemClick: (SheetTab) -> Unit,
) {
    val defaultIndicator =
        @Composable { tabPositions: List<TabPosition> ->
            if (selectedTabIndex < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                )
            }
        }

    TabRow(
        modifier =
            modifier
                .fillMaxWidth(),
        selectedTabIndex = selectedTabIndex,
        containerColor = Color.Transparent,
        indicator = if (isExpand) defaultIndicator else emptyIndicator,
        divider = if (isExpand) defaultDivider else emptyDivider,
    ) {
        items.forEachIndexed { index, item ->
            val source = remember { MutableInteractionSource() }
            LaunchedEffect(source, isExpand) {
                if (isExpand) return@LaunchedEffect

                source.interactions.collect {
                    when (it) {
                        is PressInteraction.Press -> onItemPressed(item)
                        else -> Unit
                    }
                }
            }

            Tab(
                selected = index == selectedTabIndex,
                selectedContentColor =
                    if (isExpand) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                text = @Composable {
                    Text(
                        text = stringResource(item.getLabel()),
                    )
                },
                interactionSource = source,
                onClick = {
                    onItemClick(item)
                },
            )
        }
    }
}

private fun SheetTab.getLabel() =
    when (this) {
        SheetTab.NEXT_SONG -> Res.string.play_queue
        SheetTab.LYRICS -> Res.string.lyrics
    }
