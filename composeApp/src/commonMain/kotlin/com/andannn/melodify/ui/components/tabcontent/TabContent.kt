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
import com.andannn.melodify.core.data.model.SortOption
import com.andannn.melodify.core.data.model.SortRule
import com.andannn.melodify.core.data.model.browsableOrPlayable
import com.andannn.melodify.ui.common.widgets.ExtraPaddingBottom
import com.andannn.melodify.ui.common.widgets.ListTileItemView
import com.andannn.melodify.ui.components.tabcontent.header.IdBasedGroupHeader
import com.andannn.melodify.ui.components.tabcontent.header.NameBasedGroupHeader
import com.andannn.melodify.ui.components.tabcontent.header.rememberGroupHeaderPresenter

@Composable
fun TabContent(
    state: TabContentState,
    modifier: Modifier = Modifier,
) {
    LazyListContent(
        pagingItems = state.pagingItems,
        sortRule = state.groupSort,
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
    sortRule: SortRule,
    pagingItems: LazyPagingItems<AudioItemModel>,
    modifier: Modifier = Modifier,
    onMusicItemClick: (AudioItemModel) -> Unit = {},
    onShowMusicItemOption: (AudioItemModel) -> Unit = {},
) {
    val items = pagingItems.itemSnapshotList
    val primaryGroupList =
        remember(items, sortRule) {
            items.groupByType(sortRule)
        }
    LazyColumn(
        modifier = modifier,
    ) {
        primaryGroupList.forEachIndexed { primaryGroupIndex, (primaryHeader, secondaryGroupList) ->
            if (primaryHeader != null) {
                stickyHeader(primaryHeader.hashCode()) {
                    Header(isPrimary = true, headerItem = primaryHeader)
                }
            }

            secondaryGroupList.forEachIndexed { secondaryGroupIndex, (secondaryHeader, items) ->
                if (secondaryHeader != null) {
                    stickyHeader((primaryHeader to secondaryHeader).hashCode()) {
                        Header(
                            modifier = Modifier.padding(start = 8.dp),
                            isPrimary = false,
                            headerItem = secondaryHeader,
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
                    pagingItems[primaryGroupList.flattenIndex(primaryGroupIndex, secondaryGroupIndex, index)]

                    val showTrackNum = sortRule.showTrackNum
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
    headerItem: HeaderItem,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        when (headerItem) {
            is HeaderItem.ID -> {
                val presenter = rememberGroupHeaderPresenter(headerItem)
                IdBasedGroupHeader(
                    state = presenter.present(),
                    isPrimary = isPrimary,
                )
            }

            is HeaderItem.Name -> {
                NameBasedGroupHeader(
                    item = headerItem,
                    isPrimary = isPrimary,
                )
            }
        }
    }
}

sealed class HeaderItem(
    open val groupType: GroupType,
) {
    data class ID(
        val id: String,
        override val groupType: GroupType,
    ) : HeaderItem(groupType)

    data class Name(
        val name: String,
        override val groupType: GroupType,
    ) : HeaderItem(groupType)
}

private data class PrimaryGroup(
    val headerItem: HeaderItem?,
    val content: List<ContentGroup>,
)

private data class ContentGroup(
    val headerItem: HeaderItem?,
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

private fun List<AudioItemModel?>.groupByType(sortRule: SortRule): List<PrimaryGroup> =
    groupByType(sortRule.primaryGroupSort.toSortType())
        .map { (headerItem, contentList) ->
            val primaryHeader = headerItem
            val items = contentList.groupByType(sortRule.secondaryGroupSort.toSortType())

            PrimaryGroup(
                headerItem = primaryHeader,
                content = items,
            )
        }

private fun List<AudioItemModel?>.groupByType(groupType: GroupType): List<ContentGroup> =
    this
        .filterNotNull()
        .groupBy {
            it.keyOf(groupType)
        }.map { (key, value) ->
            ContentGroup(
                headerItem = groupType.toHeader(key),
                content = value,
            )
        }

private fun AudioItemModel.keyOf(groupType: GroupType) =
    when (groupType) {
        GroupType.ARTIST -> artistId
        GroupType.ALBUM -> albumId
        GroupType.TITLE -> name[0].toString()
        GroupType.NONE -> null
    }

private fun SortOption.toSortType() =
    when (this) {
        is SortOption.Album -> GroupType.ALBUM
        is SortOption.Title -> GroupType.TITLE
        is SortOption.Artist -> GroupType.ARTIST
        SortOption.NONE -> GroupType.NONE
        is SortOption.TrackNum -> error("TrackNum is not supported")
    }

private fun GroupType.toHeader(key: String?): HeaderItem? =
    when (this) {
        GroupType.ARTIST ->
            HeaderItem.ID(
                id = key ?: error("key is null"),
                groupType = this,
            )

        GroupType.ALBUM ->
            HeaderItem.ID(
                id = key ?: error("key is null"),
                groupType = this,
            )

        GroupType.TITLE ->
            HeaderItem.Name(
                name = key.toString(),
                groupType = this,
            )

        GroupType.NONE -> null
    }
