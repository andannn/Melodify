/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.search.suggestion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.ui.components.common.LocalRepository
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.presenter.Presenter
import kotlinx.coroutines.launch

@Composable
internal fun rememberSuggestionsPresenter(
    query: String,
    repository: Repository = LocalRepository.current,
) = remember(
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
) : Presenter<SuggestionsUiState> {
    private val userPreferenceRepository = repository.userPreferenceRepository

    @Composable
    override fun present(): SuggestionsUiState {
        val scope = rememberCoroutineScope()
        val initialState =
            if (query.isEmpty()) SuggestionsState.LoadingHistory else SuggestionsState.LoadingSuggestion

        var state by rememberRetained {
            mutableStateOf(initialState)
        }
        if (initialState is SuggestionsState.LoadingHistory) {
            scope.launch {
                state =
                    SuggestionsState.HistoryLoaded(
                        userPreferenceRepository.getAllSearchHistory(),
                    )
            }
        } else {
            scope.launch {
                val result = repository.mediaContentRepository.searchContent("$query*")
                if (result.isEmpty()) {
                    state = SuggestionsState.NoSuggestion
                } else {
                    // TODO: use string similarity to find best matched items
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
        return SuggestionsUiState(state)
    }
}

data class SuggestionsUiState(
    val state: SuggestionsState,
) : CircuitUiState

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
