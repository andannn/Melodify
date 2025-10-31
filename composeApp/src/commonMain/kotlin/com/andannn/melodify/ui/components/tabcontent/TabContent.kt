/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.tabcontent

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.DisplaySetting
import com.andannn.melodify.core.data.model.GroupKey
import com.andannn.melodify.core.data.model.SortOption
import com.andannn.melodify.core.data.model.browsableOrPlayable
import com.andannn.melodify.core.data.model.keyOf
import com.andannn.melodify.ui.components.tabcontent.header.IdBasedGroupHeader
import com.andannn.melodify.ui.components.tabcontent.header.NameBasedGroupHeader
import com.andannn.melodify.ui.components.tabcontent.header.rememberGroupHeaderPresenter
import com.andannn.melodify.ui.widgets.ExtraPaddingBottom
import com.andannn.melodify.ui.widgets.ListTileItemView

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
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LazyListContent(
    displaySetting: DisplaySetting,
    pagingItems: LazyPagingItems<AudioItemModel>,
    modifier: Modifier = Modifier,
    onMusicItemClick: (AudioItemModel) -> Unit = {},
    onShowMusicItemOption: (AudioItemModel) -> Unit = {},
) {
    val items = pagingItems.itemSnapshotList
    val primaryGroupList =
        remember(items, displaySetting) {
            items.groupByType(displaySetting)
        }
    LazyColumn(
        modifier = modifier,
    ) {
        primaryGroupList.forEachIndexed { primaryGroupIndex, (primaryGroupKey, secondaryGroupList) ->
            if (primaryGroupKey != null) {
                stickyHeader(primaryGroupKey.hashCode()) {
                    Header(isPrimary = true, groupKey = primaryGroupKey)
                }
            }

            secondaryGroupList.forEachIndexed { secondaryGroupIndex, (secondaryHeader, items) ->
                if (secondaryHeader != null) {
                    stickyHeader((primaryGroupKey to secondaryHeader).hashCode()) {
                        Header(
                            modifier = Modifier.padding(start = 8.dp),
                            isPrimary = false,
                            groupKey = secondaryHeader,
                        )
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

                    val showTrackNum = displaySetting.showTrackNum
                    ListTileItemView(
                        modifier = Modifier.padding(start = 12.dp),
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

                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }

        item { ExtraPaddingBottom() }
    }
}

@Composable
private fun Header(
    isPrimary: Boolean,
    groupKey: GroupKey,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        if (groupKey.isIdBased()) {
            val presenter = rememberGroupHeaderPresenter(groupKey)
            IdBasedGroupHeader(
                state = presenter.present(),
                isPrimary = isPrimary,
            )
        } else {
            val name =
                when (groupKey) {
                    is GroupKey.Title -> groupKey.firstCharacterString
                    is GroupKey.Year -> groupKey.year
                    else -> return
                }
            NameBasedGroupHeader(
                name = name,
                isPrimary = isPrimary,
            )
        }
    }
}

private fun GroupKey.isIdBased(): Boolean =
    this is GroupKey.Album ||
        this is GroupKey.Artist ||
        this is GroupKey.Genre

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
