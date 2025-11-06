/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.tabcontent

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.paging.compose.LazyPagingItems
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.DisplaySetting
import com.andannn.melodify.core.data.model.GroupKey
import com.andannn.melodify.core.data.model.SortOption
import com.andannn.melodify.core.data.model.browsableOrPlayable
import com.andannn.melodify.core.data.model.keyOf
import com.andannn.melodify.model.OptionItem
import com.andannn.melodify.ui.components.tabcontent.header.GroupHeader
import com.andannn.melodify.ui.widgets.ExtraPaddingBottom
import com.andannn.melodify.ui.widgets.ListTileItemView
import io.github.aakira.napier.Napier

@Composable
fun TabContent(
    state: TabContentState,
    modifier: Modifier = Modifier,
) {
    LazyListContent(
        pagingItems = state.pagingItems,
        displaySetting = state.groupSort,
        modifier = modifier.fillMaxSize(),
        onMusicItemClick = {
            state.eventSink.invoke(TabContentEvent.OnPlayMusic(it))
        },
        onShowMusicItemOption = {
            state.eventSink.invoke(TabContentEvent.OnShowMusicItemOption(it))
        },
        onGroupOptionClick = { optionItem, groupKeyList ->
            state.eventSink.invoke(TabContentEvent.OnGroupOptionClick(optionItem, groupKeyList))
        },
        onGroupItemClick = { groupKeyList ->
            state.eventSink.invoke(TabContentEvent.OnGroupItemClick(groupKeyList))
        },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LazyListContent(
    displaySetting: DisplaySetting?,
    pagingItems: LazyPagingItems<AudioItemModel>,
    modifier: Modifier = Modifier,
    onGroupOptionClick: (item: OptionItem, List<GroupKey?>) -> Unit,
    onGroupItemClick: (List<GroupKey?>) -> Unit = {},
    onMusicItemClick: (AudioItemModel) -> Unit = {},
    onShowMusicItemOption: (AudioItemModel) -> Unit = {},
) {
    val items = pagingItems.itemSnapshotList
    val primaryGroupList =
        remember(items, displaySetting) {
            displaySetting ?.let { items.groupByType(displaySetting) } ?: emptyList()
        }
    LazyColumn(
        modifier = modifier,
    ) {
        primaryGroupList.forEachIndexed { primaryGroupIndex, (primaryGroupKey, secondaryGroupList) ->
            if (primaryGroupKey != null) {
                stickyHeader(primaryGroupKey.hashCode()) {
                    GroupHeader(
                        isPrimary = true,
                        groupKey = primaryGroupKey,
                        onGroupOptionSelected = { onGroupOptionClick(it, listOf(primaryGroupKey)) },
                        onGroupHeaderClick = { onGroupItemClick(listOf(primaryGroupKey)) },
                    )
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
                            GroupHeader(
                                isPrimary = false,
                                groupKey = secondaryGroupKey,
                                onGroupOptionSelected = {
                                    onGroupOptionClick(
                                        it,
                                        listOf(primaryGroupKey, secondaryGroupKey),
                                    )
                                },
                                onGroupHeaderClick = {
                                    onGroupItemClick(
                                        listOf(
                                            primaryGroupKey,
                                            secondaryGroupKey,
                                        ),
                                    )
                                },
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
                            val needConnection = secondaryGroupIndex != secondaryGroupList.lastIndex
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
                        ListTileItemView(
                            paddingValues =
                                PaddingValues(
                                    top = 4.dp,
                                    bottom = 4.dp,
                                ),
                            playable = item.browsableOrPlayable,
                            isActive = false,
                            albumArtUri = item.artWorkUri,
                            title = item.name,
                            trackNum = item.cdTrackNumber.takeIf { showTrackNum },
                            onMusicItemClick = {
                                onMusicItemClick.invoke(item)
                            },
                            onOptionButtonClick = {
                                onShowMusicItemOption(item)
                            },
                        )
                    }
                }
            }
        }

        item { ExtraPaddingBottom() }
    }
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
    val content: List<AudioItemModel>,
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

private fun List<AudioItemModel?>.groupByType(displaySetting: DisplaySetting): List<PrimaryGroup> =
    groupByType(displaySetting.primaryGroupSort)
        .map { (headerItem, contentList) ->
            val primaryHeader = headerItem
            val items = contentList.groupByType(displaySetting.secondaryGroupSort)

            PrimaryGroup(
                headerItem = primaryHeader,
                content = items,
            )
        }

private fun List<AudioItemModel?>.groupByType(sortOption: SortOption): List<SecondaryGroup> =
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
