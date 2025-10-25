/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.tabcontent

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.browsableOrPlayable
import com.andannn.melodify.ui.common.widgets.ExtraPaddingBottom
import com.andannn.melodify.ui.common.widgets.ListTileItemView
import com.andannn.melodify.ui.components.tabcontent.header.IdBasedGroupHeader
import com.andannn.melodify.ui.components.tabcontent.header.NameBasedGroupHeader
import com.andannn.melodify.ui.components.tabcontent.header.rememberGroupHeaderPresenter
import kotlinx.collections.immutable.ImmutableList

@Composable
fun TabContent(
    state: TabContentState,
    modifier: Modifier = Modifier,
) {
    LazyListContent(
        contentGroup = state.contentGroup,
        listState = state.listState,
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
    listState: LazyListState,
    contentGroup: ImmutableList<ContentGroup>,
    modifier: Modifier = Modifier,
    onMusicItemClick: (AudioItemModel) -> Unit = {},
    onShowMusicItemOption: (AudioItemModel) -> Unit = {},
) {
    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 5.dp),
    ) {
        contentGroup.forEach { (headerItem, mediaItems) ->
            if (headerItem != null) {
                stickyHeader(headerItem.hashCode()) {
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

            items(
                items = mediaItems,
                key = { item ->
                    item.hashCode()
                },
            ) { item ->
                ListTileItemView(
                    playable = item.browsableOrPlayable,
                    isActive = false,
                    albumArtUri = item.artWorkUri,
                    title = item.name,
                    trackNum = item.cdTrackNumber.takeIf { headerItem?.groupType == GroupType.ALBUM },
                    onMusicItemClick = {
                        onMusicItemClick.invoke(item)
                    },
                    onOptionButtonClick = {
                        onShowMusicItemOption(item)
                    },
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }

        item { ExtraPaddingBottom() }
    }
}
