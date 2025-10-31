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
import com.andannn.melodify.core.data.model.GroupKey
import com.andannn.melodify.core.data.model.SortOption
import com.andannn.melodify.core.data.model.SortRule
import com.andannn.melodify.core.data.model.browsableOrPlayable
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
                    pagingItems[
                        primaryGroupList.flattenIndex(
                            primaryGroupIndex,
                            secondaryGroupIndex,
                            index,
                        ),
                    ]

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
    open val groupKey: GroupKey,
) {
    data class ID(
        val id: String,
        override val groupKey: GroupKey,
    ) : HeaderItem(groupKey)

    data class Name(
        val name: String,
        override val groupKey: GroupKey,
    ) : HeaderItem(groupKey)
}

private data class PrimaryGroup(
    val headerItem: HeaderItem?,
    val content: List<SecondaryGroup>,
)

private data class SecondaryGroup(
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
    groupByType(sortRule.primaryGroupSort)
        .map { (headerItem, contentList) ->
            val primaryHeader = headerItem
            val items = contentList.groupByType(sortRule.secondaryGroupSort)

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
                headerItem = key?.toHeader(),
                content = value,
            )
        }

private fun AudioItemModel.keyOf(sortOption: SortOption): GroupKey? =
    when (sortOption) {
        is SortOption.Album -> GroupKey.ALBUM(albumId)
        is SortOption.Artist -> GroupKey.ARTIST(artistId)
        is SortOption.Genre -> GroupKey.Genre(genreId)
        is SortOption.ReleaseYear -> GroupKey.YEAR(releaseYear)
        is SortOption.Title -> GroupKey.TITLE(name[0].toString())
        SortOption.NONE -> null
        is SortOption.TrackNum -> error("Not support")
    }

private fun GroupKey.toHeader(): HeaderItem? =
    when (this) {
        is GroupKey.ARTIST ->
            HeaderItem.ID(
                id = artistId,
                groupKey = this,
            )

        is GroupKey.ALBUM ->
            HeaderItem.ID(
                id = albumId,
                groupKey = this,
            )

        is GroupKey.TITLE ->
            HeaderItem.Name(
                name = firstCharacterString,
                groupKey = this,
            )

        is GroupKey.Genre ->
            HeaderItem.ID(
                id = genreId,
                groupKey = this,
            )

        is GroupKey.YEAR ->
            HeaderItem.Name(
                name = year,
                groupKey = this,
            )
    }
