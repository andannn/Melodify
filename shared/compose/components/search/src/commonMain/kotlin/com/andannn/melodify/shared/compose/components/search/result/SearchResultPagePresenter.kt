/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.components.search.result

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import com.andannn.melodify.domain.Repository
import com.andannn.melodify.domain.model.MatchedContentTitle
import com.andannn.melodify.domain.model.MediaType
import com.andannn.melodify.shared.compose.common.LocalRepository
import com.andannn.melodify.shared.compose.popup.LocalPopupHostState
import io.github.andannn.RetainedModel
import io.github.andannn.popup.PopupHostState
import io.github.andannn.retainRetainedModel
import kotlinx.coroutines.launch

@Stable
sealed interface SearchState {
    data object Searching : SearchState

    data class Result(
        val result: Map<MediaType, List<MatchedContentTitle>>,
    ) : SearchState

    data object NoObject : SearchState
}

@Composable
internal fun retainSearchResultPageModel(
    query: String,
    repository: Repository = LocalRepository.current,
    popupHostState: PopupHostState = LocalPopupHostState.current,
) = retainRetainedModel(
    query,
    repository,
    popupHostState,
) {
    SearchResultPagePresenter(
        query,
        repository,
    )
}

internal class SearchResultPagePresenter(
    private val query: String,
    private val repository: Repository,
) : RetainedModel() {
    var state = mutableStateOf<SearchState>(SearchState.Searching)

    init {
        retainedScope.launch {
            val result = repository.getMatchedContentTitle(query)
            if (result.isEmpty()) {
                state.value = SearchState.NoObject
            } else {
                state.value = SearchState.Result(result.groupBy { it.type })
            }
        }
    }
}
