/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.components.search.common

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andannn.melodify.domain.model.MatchedContentTitle
import com.andannn.melodify.domain.model.MediaItemModel
import com.andannn.melodify.domain.model.MediaType
import com.andannn.melodify.shared.compose.common.widgets.ExtraPaddingBottom
import com.andannn.melodify.shared.compose.components.library.item.MediaLibraryItem
import melodify.shared.compose.resource.generated.resources.Res
import melodify.shared.compose.resource.generated.resources.album_page_title
import melodify.shared.compose.resource.generated.resources.artist_page_title
import melodify.shared.compose.resource.generated.resources.audio_page_title
import melodify.shared.compose.resource.generated.resources.genre_title
import melodify.shared.compose.resource.generated.resources.playlist_page_title
import melodify.shared.compose.resource.generated.resources.video_page_title
import org.jetbrains.compose.resources.stringResource
import kotlin.collections.component1
import kotlin.collections.component2

fun LazyListScope.searchResultItems(
    showOptions: Boolean,
    itemsMap: Map<MediaType, List<MatchedContentTitle>>,
    onResultItemClick: (MediaItemModel) -> Unit,
) {
    itemsMap.forEach { (type, suggestions) ->
        item {
            Text(
                modifier = Modifier.padding(horizontal = 8.dp).padding(top = 24.dp),
                text = stringResource(type.label()),
                style = MaterialTheme.typography.labelLarge,
            )
        }
        items(
            suggestions,
            key = { type to it.id },
        ) { matchedContent ->
            MediaLibraryItem(
                contentId = matchedContent.id,
                contentType = type,
                showOptions = showOptions,
                onItemClick = {
                    onResultItemClick(it)
                },
            )
        }
    }

    item {
        ExtraPaddingBottom()
    }
}

private fun MediaType.label() =
    when (this) {
        MediaType.AUDIO -> Res.string.audio_page_title
        MediaType.VIDEO -> Res.string.video_page_title
        MediaType.ALBUM -> Res.string.album_page_title
        MediaType.ARTIST -> Res.string.artist_page_title
        MediaType.GENRE -> Res.string.genre_title
        MediaType.PLAYLIST -> Res.string.playlist_page_title
    }
