/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.search.suggestion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.ui.core.LocalRepository
import com.andannn.melodify.ui.core.RetainedPresenter
import com.andannn.melodify.ui.core.retainPresenter
import kotlinx.coroutines.launch

@Composable
internal fun retainSuggestionsPresenter(
    query: String,
    repository: Repository = LocalRepository.current,
) = retainPresenter(
    query,
    repository,
) {
    SuggestionsPresenter(
        query = query,
        repository = repository,
    )
}

internal class SuggestionsPresenter(
    private val query: String,
    private val repository: Repository,
) : RetainedPresenter<SuggestionsUiState>() {
    val initialState =
        if (query.isEmpty()) SuggestionsState.LoadingHistory else SuggestionsState.LoadingSuggestion
    private var state by mutableStateOf(initialState)

    init {
        if (initialState is SuggestionsState.LoadingHistory) {
            retainedScope.launch {
                state =
                    SuggestionsState.HistoryLoaded(
                        repository.getAllSearchHistory(),
                    )
            }
        } else {
            retainedScope.launch {
                val result = repository.searchContent("$query*")
                if (result.isEmpty()) {
                    state = SuggestionsState.NoSuggestion
                } else {
                    val bestMatchedItems =
                        result.filter {
                            it.name == query
                        }
                    state =
                        SuggestionsState.SuggestionLoaded(
                            suggestions = result.map { it.name }.distinct(),
                            bestMatchedItems = bestMatchedItems,
                        )
                }
            }
        }
    }

    @Composable
    override fun present(): SuggestionsUiState = SuggestionsUiState(state)
}

@Stable
data class SuggestionsUiState(
    val state: SuggestionsState,
)

@Stable
sealed interface SuggestionsState {
    /**
     * When query string is empty, show history search suggestions.
     */
    sealed class History : SuggestionsState

    data object LoadingHistory : History()

    data class HistoryLoaded(
        val searchWordList: List<String> = emptyList(),
    ) : History()

    /**
     * When query string is not empty, show search suggestions.
     */
    sealed class Suggestion : SuggestionsState

    /**
     * Loading search suggestions.
     */
    data object LoadingSuggestion : Suggestion()

    /**
     * No search suggestions.
     */
    data object NoSuggestion : Suggestion()

    /**
     * Showing search suggestions.
     */
    data class SuggestionLoaded(
        val suggestions: List<String>,
        val bestMatchedItems: List<MediaItemModel>,
    ) : Suggestion()
}
