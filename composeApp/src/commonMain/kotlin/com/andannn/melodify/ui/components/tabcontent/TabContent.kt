/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.tabcontent

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
    val contentGroup =
        remember(items, sortRule) {
            items.toGroup(sortRule)
        }
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 5.dp),
    ) {
        contentGroup.forEachIndexed { groupIndex, (primaryHeader, contents) ->
            if (primaryHeader != null) {
                stickyHeader(primaryHeader.hashCode()) {
                    Header(isPrimary = true, headerItem = primaryHeader)
                }
            }

            contents.forEachIndexed { index, (secondaryHeader, contents) ->
                if (secondaryHeader != null) {
                    stickyHeader((primaryHeader to secondaryHeader).hashCode()) {
                        Header(isPrimary = false, headerItem = secondaryHeader)
                    }
                }

                itemsIndexed(
                    items = contents,
                    key = { index, item ->
                        item.hashCode()
                    },
                ) { index, item ->
//                    pagingItems[contentGroup.flattenIndex(groupIndex, index)]

                    ListTileItemView(
                        playable = item.browsableOrPlayable,
                        isActive = false,
                        albumArtUri = item.artWorkUri,
                        title = item.name,
                        trackNum = item.cdTrackNumber,
                        onMusicItemClick = {
                            onMusicItemClick.invoke(item)
                        },
                        onOptionButtonClick = {
                            onShowMusicItemOption(item)
                        },
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
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
                )
            }

            is HeaderItem.Name -> {
                NameBasedGroupHeader(
                    item = headerItem,
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

private data class ContentGroup(
    val headerItem: HeaderItem?,
    val content: List<AudioItemModel>,
)

private fun List<ContentGroup>.flattenIndex(
    groupIndex: Int,
    itemIndex: Int,
): Int {
    var ret = 0
    for (i in 0..groupIndex) {
        ret +=
            if (i == groupIndex) {
                itemIndex
            } else {
                this[i].content.size
            }
    }
    return ret
}

private fun List<AudioItemModel?>.toGroup(sortRule: SortRule) =
    toGroup(sortRule.primaryGroupSort.toSortType())
        .map { (headerItem, contentList) ->
            val primaryHeader = headerItem
            val items = contentList.toGroup(sortRule.secondaryGroupSort.toSortType())

            primaryHeader to items
        }

private fun List<AudioItemModel?>.toGroup(groupType: GroupType): List<ContentGroup> =
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
