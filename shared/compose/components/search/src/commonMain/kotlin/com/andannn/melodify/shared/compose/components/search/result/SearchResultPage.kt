/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.components.search.result

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.andannn.melodify.domain.model.MediaItemModel
import com.andannn.melodify.shared.compose.components.search.common.searchResultItems

@Composable
fun SearchResultPage(
    query: String,
    modifier: Modifier = Modifier,
    onResultItemClick: (MediaItemModel) -> Unit = {},
) {
    val state =
        retainSearchResultPageModel(
            query = query,
        ).state

    SearchPageContent(
        modifier = modifier,
        searchedResult = state.value,
        onResultItemClick = onResultItemClick,
    )
}

@Composable
private fun SearchPageContent(
    modifier: Modifier = Modifier,
    searchedResult: SearchState,
    onResultItemClick: (MediaItemModel) -> Unit = {},
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (searchedResult) {
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
        searchResultItems(
            showOptions = true,
            itemsMap = result.result,
            onResultItemClick = onResultItemClick,
        )
    }
}
