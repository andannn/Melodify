/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.components.search.result

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.shared.compose.components.library.item.MediaLibraryItem
import com.andannn.melodify.shared.compose.components.search.SearchState

@Composable
internal fun SearchPageView(
    modifier: Modifier = Modifier,
    searchedResult: SearchState,
    onResultItemClick: (MediaItemModel) -> Unit = {},
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (searchedResult) {
            SearchState.Init,
            SearchState.NoObject,
            -> {
            }

            SearchState.Searching -> {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }

            is SearchState.Result -> {
                SearchPageContent(
                    modifier = Modifier,
                    result = searchedResult,
                    onResultItemClick = onResultItemClick,
                )
            }
        }
    }
}

@Composable
private fun SearchPageContent(
    modifier: Modifier = Modifier,
    result: SearchState.Result,
    onResultItemClick: (MediaItemModel) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier,
    ) {
        if (result.albums.isNotEmpty()) {
            item {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
                    text = "Album",
                    style = MaterialTheme.typography.titleLarge,
                )
            }

            items(
                items = result.albums,
                key = { it.id },
            ) {
                MediaLibraryItem(
                    mediaItemModel = it,
                    onItemClick = {
                        onResultItemClick(it)
                    },
                )
            }
        }

        if (result.audios.isNotEmpty()) {
            item {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
                    text = "Audios",
                    style = MaterialTheme.typography.titleLarge,
                )
            }

            items(
                items = result.audios,
                key = { it.id },
            ) {
                MediaLibraryItem(
                    mediaItemModel = it,
                    onItemClick = {
                        onResultItemClick(it)
                    },
                )
            }
        }

        if (result.artists.isNotEmpty()) {
            item {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
                    text = "Artists",
                    style = MaterialTheme.typography.titleLarge,
                )
            }

            items(
                items = result.artists,
                key = { it.id },
            ) {
                MediaLibraryItem(
                    mediaItemModel = it,
                    onItemClick = {
                        onResultItemClick(it)
                    },
                )
            }
        }
    }
}
