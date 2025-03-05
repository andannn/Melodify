package com.andannn.melodify.ui.components.search.result

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
import com.andannn.melodify.ui.components.search.SearchState
import com.andannn.melodify.ui.components.common.MediaItemWithOptionAction

@Composable
internal fun SearchPageView(
    modifier: Modifier = Modifier,
    searchedResult: SearchState,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (searchedResult) {
            SearchState.Init,
            SearchState.NoObject -> {

            }

            SearchState.Searching -> {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }

            is SearchState.Result -> SearchPageContent(
                modifier = Modifier,
                result = searchedResult
            )
        }
    }
}

@Composable
private fun SearchPageContent(
    modifier: Modifier = Modifier,
    result: SearchState.Result,
) {
    LazyColumn(
        modifier = modifier
    ) {
        if (result.albums.isNotEmpty()) {
            item {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
                    text = "Album",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            items(
                items = result.albums,
                key = { it.id }
            ) {
                MediaItemWithOptionAction(
                    mediaItemModel = it,
                    onItemClick = {}
                )
            }
        }

        if (result.audios.isNotEmpty()) {
            item {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
                    text = "Audios",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            items(
                items = result.audios,
                key = { it.id }
            ) {
                MediaItemWithOptionAction(
                    mediaItemModel = it,
                    onItemClick = {}
                )
            }
        }

        if (result.artists.isNotEmpty()) {
            item {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
                    text = "Artists",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            items(
                items = result.artists,
                key = { it.id }
            ) {
                MediaItemWithOptionAction(
                    mediaItemModel = it,
                    onItemClick = {}
                )
            }
        }
    }
}

