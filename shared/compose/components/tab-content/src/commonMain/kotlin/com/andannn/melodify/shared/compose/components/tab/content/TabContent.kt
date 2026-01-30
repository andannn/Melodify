/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.components.tab.content

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andannn.melodify.domain.model.AudioItemModel
import com.andannn.melodify.domain.model.AudioTrackStyle
import com.andannn.melodify.domain.model.GroupKey
import com.andannn.melodify.domain.model.MediaItemModel
import com.andannn.melodify.domain.model.SortOption
import com.andannn.melodify.domain.model.Tab
import com.andannn.melodify.domain.model.TabSortRule
import com.andannn.melodify.domain.model.VideoItemModel
import com.andannn.melodify.domain.model.browsableOrPlayable
import com.andannn.melodify.domain.model.groupKeyOf
import com.andannn.melodify.shared.compose.common.mock.MockData
import com.andannn.melodify.shared.compose.common.theme.MelodifyTheme
import com.andannn.melodify.shared.compose.common.widgets.ExtraPaddingBottom
import com.andannn.melodify.shared.compose.common.widgets.ListTileItemView
import com.andannn.melodify.shared.compose.components.tab.content.header.GroupHeaderContainer
import com.andannn.melodify.shared.compose.components.tab.content.video.VideoListTileItemView
import com.andannn.melodify.shared.compose.components.tab.content.widget.GroupConnection
import com.andannn.melodify.shared.compose.components.tab.content.widget.GroupIndicator
import com.andannn.melodify.shared.compose.components.tab.content.widget.NoneStepScrollIndicator
import kotlinx.coroutines.launch

@Composable
fun TabContent(
    state: TabContentState,
    modifier: Modifier = Modifier,
) {
    LazyListContent(
        selectedTab = state.selectedTab,
        audioTrackStyle = state.audioTrackStyle,
        itemSnapshotList = state.pagingItems.itemSnapshotList,
        onTriggerReadOfIndex = {
            state.pagingItems[it]
        },
        tabSortRule = state.tabSortRule,
        modifier = modifier.fillMaxSize(),
        onMediaItemClick = {
            state.eventSink.invoke(TabContentEvent.OnPlayMedia(it))
        },
        onShowMediaItemOption = {
            state.eventSink.invoke(TabContentEvent.OnShowMediaItemOption(it))
        },
        onBuildGroupHeader = { groupKey, parentHeaderGroupKey ->
            GroupHeaderContainer(
                selectedTab = state.selectedTab,
                tabSortRule = state.tabSortRule,
                groupKey = groupKey,
                parentHeaderGroupKey = parentHeaderGroupKey,
                onGroupItemClick = { groupKeyList ->
                    state.eventSink.invoke(TabContentEvent.OnGroupItemClick(groupKeyList))
                },
            )
        },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LazyListContent(
    selectedTab: Tab?,
    tabSortRule: TabSortRule?,
    audioTrackStyle: AudioTrackStyle?,
    itemSnapshotList: List<MediaItemModel?>,
    modifier: Modifier = Modifier,
    onMediaItemClick: (MediaItemModel) -> Unit = {},
    onShowMediaItemOption: (MediaItemModel) -> Unit = {},
    onTriggerReadOfIndex: (Int) -> Unit = {},
    onBuildGroupHeader: @Composable (
        groupKey: GroupKey,
        parentHeaderGroupKey: GroupKey?,
    ) -> Unit = { _, _ -> },
) {
    val primaryGroupList =
        remember(itemSnapshotList, tabSortRule) {
            tabSortRule?.let { itemSnapshotList.groupByType(tabSortRule) } ?: emptyList()
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
                        onBuildGroupHeader(primaryGroupKey, null)
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
                                onBuildGroupHeader(
                                    secondaryGroupKey,
                                    primaryGroupKey,
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

                        var headerCount = 0
                        if (primaryGroupKey != null) headerCount++
                        if (secondaryGroupKey != null) headerCount++

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
                                is AudioItemModel -> {
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
                                        trackNum = item.cdTrackNumber.takeIf { audioTrackStyle == AudioTrackStyle.TRACK_NUMBER },
                                        onItemClick = {
                                            onMediaItemClick(item)
                                        },
                                        onOptionButtonClick = {
                                            onShowMediaItemOption(item)
                                        },
                                    )
                                }

                                is VideoItemModel -> {
                                    VideoListTileItemView(
                                        item = item,
                                        tab = selectedTab,
                                        onItemClick = {
                                            onMediaItemClick(item)
                                        },
                                        onOptionButtonClick = {
                                            onShowMediaItemOption(item)
                                        },
                                    )
                                }

                                else -> {
                                    error("not supported")
                                }
                            }
                        }

                        SideEffect {
                            onTriggerReadOfIndex(
                                primaryGroupList.flattenIndex(
                                    primaryGroupIndex,
                                    secondaryGroupIndex,
                                    index,
                                ),
                            )
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

private fun List<MediaItemModel?>.groupByType(tabSortRule: TabSortRule): List<PrimaryGroup> =
    groupByType(tabSortRule.primaryGroupSort)
        .map { (headerItem, contentList) ->
            val primaryHeader = headerItem
            val items = contentList.groupByType(tabSortRule.secondaryGroupSort)

            PrimaryGroup(
                headerItem = primaryHeader,
                content = items,
            )
        }

private fun List<MediaItemModel?>.groupByType(sortOption: SortOption): List<SecondaryGroup> =
    this
        .filterNotNull()
        .groupBy {
            it.groupKeyOf(sortOption)
        }.map { (key, value) ->
            SecondaryGroup(
                headerItem = key,
                content = value,
            )
        }

@Composable
@Preview
private fun LazyListContentAlbumASCPreview() {
    MelodifyTheme {
        LazyListContent(
            selectedTab = null,
            audioTrackStyle = null,
            tabSortRule = TabSortRule.Preset.Audio.AlbumASC,
            itemSnapshotList = MockData.medias,
            onBuildGroupHeader = { _, _ ->
                Box(modifier = Modifier.fillMaxWidth().height(50.dp).background(Color.Red))
            },
        )
    }
}

@Composable
@Preview
private fun LazyListContentArtistASCPreview() {
    MelodifyTheme {
        LazyListContent(
            selectedTab = null,
            audioTrackStyle = null,
            tabSortRule = TabSortRule.Preset.Audio.ArtistASC,
            itemSnapshotList = MockData.medias,
            onBuildGroupHeader = { _, _ ->
                Box(modifier = Modifier.fillMaxWidth().height(50.dp).background(Color.Red))
            },
        )
    }
}

@Composable
@Preview
private fun LazyListContentArtistAlbumASCPreview() {
    MelodifyTheme {
        LazyListContent(
            selectedTab = null,
            audioTrackStyle = null,
            tabSortRule = TabSortRule.Preset.Audio.ArtistAlbumASC,
            itemSnapshotList = MockData.medias,
            onBuildGroupHeader = { _, _ ->
                Box(modifier = Modifier.fillMaxWidth().height(50.dp).background(Color.Red))
            },
        )
    }
}
