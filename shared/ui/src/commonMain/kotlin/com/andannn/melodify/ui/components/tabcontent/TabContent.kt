/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.tabcontent

import androidx.annotation.IntRange
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollIndicatorState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SliderState
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalSlider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.core.data.model.DisplaySetting
import com.andannn.melodify.core.data.model.GroupKey
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.SortOption
import com.andannn.melodify.core.data.model.VideoItemModel
import com.andannn.melodify.core.data.model.browsableOrPlayable
import com.andannn.melodify.core.data.model.keyOf
import com.andannn.melodify.ui.components.tabcontent.header.GroupHeader
import com.andannn.melodify.ui.components.tabcontent.header.GroupInfo
import com.andannn.melodify.ui.widgets.ExtraPaddingBottom
import com.andannn.melodify.ui.widgets.ListTileItemView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import melodify.shared.ui.generated.resources.Res
import melodify.shared.ui.generated.resources.outline_arrow_range_24
import org.jetbrains.compose.resources.painterResource

@Composable
fun TabContent(
    state: TabContentState,
    modifier: Modifier = Modifier,
) {
    LazyListContent(
        selectedTab = state.selectedTab,
        pagingItems = state.pagingItems,
        displaySetting = state.groupSort,
        modifier = modifier.fillMaxSize(),
        onMediaItemClick = {
            state.eventSink.invoke(TabContentEvent.OnPlayMedia(it))
        },
        onShowMediaItemOption = {
            state.eventSink.invoke(TabContentEvent.OnShowMediaItemOption(it))
        },
        onGroupItemClick = { groupKeyList ->
            state.eventSink.invoke(TabContentEvent.OnGroupItemClick(groupKeyList))
        },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LazyListContent(
    selectedTab: CustomTab?,
    displaySetting: DisplaySetting?,
    pagingItems: LazyPagingItems<MediaItemModel>,
    modifier: Modifier = Modifier,
    onMediaItemClick: (MediaItemModel) -> Unit = {},
    onShowMediaItemOption: (MediaItemModel) -> Unit = {},
    onGroupItemClick: (List<GroupKey?>) -> Unit = {},
) {
    val items = pagingItems.itemSnapshotList
    val primaryGroupList =
        remember(items, displaySetting) {
            displaySetting?.let { items.groupByType(displaySetting) } ?: emptyList()
        }

    @Composable
    fun GroupHeaderContainer(
        groupKey: GroupKey,
        parentHeaderGroupKey: GroupKey? = null,
    ) {
        val groupState =
            GroupInfo(
                groupKey = groupKey,
                parentHeaderGroupKey = parentHeaderGroupKey,
                displaySetting = displaySetting,
                selectedTab = selectedTab,
            )
        GroupHeader(
            groupInfo = groupState,
            isPrimary = parentHeaderGroupKey == null,
            onGroupHeaderClick = {
                onGroupItemClick.invoke(groupState.selection)
            },
        )
    }

    val state = rememberLazyListState()
    Box(modifier = modifier) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 4.dp),
            state = state,
        ) {
            primaryGroupList.forEachIndexed { primaryGroupIndex, (primaryGroupKey, secondaryGroupList) ->
                if (primaryGroupKey != null) {
                    stickyHeader(primaryGroupKey.hashCode()) {
                        GroupHeaderContainer(primaryGroupKey)
                    }
                }

                secondaryGroupList.forEachIndexed { secondaryGroupIndex, (secondaryGroupKey, items) ->
                    if (secondaryGroupKey != null) {
                        stickyHeader((primaryGroupKey to secondaryGroupKey).hashCode()) {
                            Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                                GroupIndicator(
                                    modifier = Modifier.width(20.dp),
                                    isLast = secondaryGroupIndex == secondaryGroupList.lastIndex,
                                )
                                GroupHeaderContainer(
                                    groupKey = secondaryGroupKey,
                                    parentHeaderGroupKey = primaryGroupKey,
                                )
                            }
                        }
                    }

                    itemsIndexed(
                        items = items,
                        key = { index, item ->
                            item.hashCode()
                        },
                    ) { index, item ->
                        // trigger item read event to load more.
                        pagingItems[
                            primaryGroupList.flattenIndex(
                                primaryGroupIndex,
                                secondaryGroupIndex,
                                index,
                            ),
                        ]
                        var headerCount = 0
                        if (primaryGroupKey != null) headerCount++
                        if (secondaryGroupKey != null) headerCount++

                        val showTrackNum = displaySetting?.showTrackNum ?: true
                        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                            if (headerCount >= 2) {
                                val needConnection =
                                    secondaryGroupIndex != secondaryGroupList.lastIndex
                                if (needConnection) {
                                    GroupConnection(modifier = Modifier.width(20.dp))
                                } else {
                                    Spacer(modifier = Modifier.width(20.dp))
                                }
                            }
                            if (headerCount >= 1) {
                                GroupIndicator(
                                    modifier = Modifier.width(20.dp),
                                    isLast = index == items.lastIndex,
                                )
                            }
                            when (item) {
                                is AudioItemModel ->
                                    ListTileItemView(
                                        paddingValues =
                                            PaddingValues(
                                                top = 4.dp,
                                                bottom = 4.dp,
                                            ),
                                        playable = item.browsableOrPlayable,
                                        isActive = false,
                                        thumbnailSourceUri = item.artWorkUri,
                                        title = item.name,
                                        trackNum = item.cdTrackNumber.takeIf { showTrackNum },
                                        onItemClick = {
                                            onMediaItemClick(item)
                                        },
                                        onOptionButtonClick = {
                                            onShowMediaItemOption(item)
                                        },
                                    )

                                is VideoItemModel ->
                                    ListTileItemView(
                                        paddingValues =
                                            PaddingValues(
                                                top = 4.dp,
                                                bottom = 4.dp,
                                            ),
                                        playable = item.browsableOrPlayable,
                                        isActive = false,
                                        thumbnailSourceUri = item.artWorkUri,
                                        title = item.name,
                                        onItemClick = {
                                            onMediaItemClick(item)
                                        },
                                        onOptionButtonClick = {
                                            onShowMediaItemOption(item)
                                        },
                                    )
                                else -> error("not supported")
                            }
                        }
                    }
                }
            }

            item { ExtraPaddingBottom() }
        }

        val scrollIndicatorState = state.scrollIndicatorState
        if (scrollIndicatorState != null) {
            val scope = rememberCoroutineScope()
            NoneStepScrollIndicator(
                modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd),
                scrollIndicatorState = scrollIndicatorState,
                onScrollBy = { scrollBy ->
                    scope.launch {
                        state.scrollBy(scrollBy)
                    }
                },
            )
        }
    }
}

@Composable
private fun NoneStepScrollIndicator(
    scrollIndicatorState: ScrollIndicatorState,
    modifier: Modifier = Modifier,
    onScrollBy: (Float) -> Unit = {},
) {
    val value by
        remember {
            derivedStateOf {
                val contentSize = scrollIndicatorState.contentSize
                val scrollOffset = scrollIndicatorState.scrollOffset
                if (contentSize == 0) {
                    0f
                } else {
                    scrollOffset
                        .toFloat()
                        .div(contentSize)
                        .coerceIn(0f, 1f)
                }
            }
        }
    val interactionSource = remember { MutableInteractionSource() }
    val isDragged by interactionSource.collectIsDraggedAsState()

    var isVisible by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(value) {
        isVisible = true
        delay(800)
        isVisible = false
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier,
    ) {
        CustomVerticalScrollIndicator(
            value = value,
            interactionSource = interactionSource,
            onValueChange = { newValue ->
                if (isDragged) {
                    val contentSize = scrollIndicatorState.contentSize
                    onScrollBy((newValue - value).times(contentSize))
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun CustomVerticalScrollIndicator(
    value: Float,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    @IntRange(from = 0) steps: Int = 0,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    onValueChangeFinished: (() -> Unit)? = null,
) {
    val state =
        remember(steps, valueRange) { SliderState(value, steps, onValueChangeFinished, valueRange) }
    state.onValueChangeFinished = onValueChangeFinished
    state.onValueChange = onValueChange
    state.value = value

    VerticalSlider(
        modifier = modifier,
        state = state,
        interactionSource = interactionSource,
        track = {
            Spacer(modifier.fillMaxHeight())
        },
        thumb = {
            val thumbTranslationX =
                with(LocalDensity.current) {
                    16.dp.toPx()
                }
            Surface(
                modifier =
                    Modifier
                        .size(48.dp)
                        .graphicsLayer {
                            translationX = thumbTranslationX
                        },
                shadowElevation = 24.dp,
                color = MaterialTheme.colorScheme.secondaryFixed,
                shape = CircleShape,
            ) {
                Image(
                    modifier =
                        Modifier.graphicsLayer {
                            scaleX = 0.4f
                            scaleY = 0.4f
                            rotationZ = 90f
                            translationX = (-thumbTranslationX).div(3)
                        },
                    painter = painterResource(Res.drawable.outline_arrow_range_24),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondaryFixed),
                    contentDescription = null,
                )
            }
        },
    )
}

@Composable
private fun GroupIndicator(
    modifier: Modifier,
    isLast: Boolean,
    color: Color = MaterialTheme.colorScheme.outlineVariant,
) {
    Spacer(
        modifier =
            modifier.fillMaxHeight().drawBehind {
                val strokeWidth = 1.dp.toPx()
                val startX = size.width.div(2f)
                val startY = 0f
                val endX = startX
                val endY = if (isLast) size.height.div(2) - size.width.div(2f) else size.height

                drawLine(color, Offset(startX, startY), Offset(endX, endY), strokeWidth)

                val arcTopLeftY = size.height.div(2) - size.width
                drawArc(
                    color = color,
                    topLeft = Offset(startX, arcTopLeftY),
                    size = Size(size.width, size.width),
                    useCenter = false,
                    startAngle = 180f,
                    sweepAngle = -90f,
                    style = Stroke(width = strokeWidth),
                )
            },
    )
}

@Composable
private fun GroupConnection(
    modifier: Modifier,
    color: Color = MaterialTheme.colorScheme.outlineVariant,
) {
    Spacer(
        modifier =
            modifier.fillMaxHeight().drawBehind {
                val strokeWidth = 1.dp.toPx()
                val startX = size.width.div(2f)
                val startY = 0f
                val endX = startX
                val endY = size.height
                drawLine(color, Offset(startX, startY), Offset(endX, endY), strokeWidth)
            },
    )
}

private data class PrimaryGroup(
    val headerItem: GroupKey?,
    val content: List<SecondaryGroup>,
)

private data class SecondaryGroup(
    val headerItem: GroupKey?,
    val content: List<MediaItemModel>,
)

private fun List<PrimaryGroup>.flattenIndex(
    primaryGroupIndex: Int,
    secondaryGroupIndex: Int,
    itemIndex: Int,
): Int {
    if (isEmpty()) return 0

    var ret = 0
    val safePrimary = primaryGroupIndex.coerceIn(0, lastIndex)
    for (p in 0 until safePrimary) {
        ret += this[p].content.sumOf { it.content.size }
    }

    val primary = this[safePrimary]
    if (primary.content.isNotEmpty()) {
        val safeSecondary = secondaryGroupIndex.coerceIn(0, primary.content.lastIndex)
        for (s in 0 until safeSecondary) {
            ret += primary.content[s].content.size
        }
    }

    ret += itemIndex.coerceAtLeast(0)
    return ret
}

private fun List<MediaItemModel?>.groupByType(displaySetting: DisplaySetting): List<PrimaryGroup> =
    groupByType(displaySetting.primaryGroupSort)
        .map { (headerItem, contentList) ->
            val primaryHeader = headerItem
            val items = contentList.groupByType(displaySetting.secondaryGroupSort)

            PrimaryGroup(
                headerItem = primaryHeader,
                content = items,
            )
        }

private fun List<MediaItemModel?>.groupByType(sortOption: SortOption): List<SecondaryGroup> =
    this
        .filterNotNull()
        .groupBy {
            it.keyOf(sortOption)
        }.map { (key, value) ->
            SecondaryGroup(
                headerItem = key,
                content = value,
            )
        }
