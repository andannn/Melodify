/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.components.search.suggestion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.andannn.melodify.domain.Repository
import com.andannn.melodify.domain.model.MatchedContentTitle
import com.andannn.melodify.domain.model.MediaItemModel
import com.andannn.melodify.domain.model.MediaType
import com.andannn.melodify.shared.compose.common.LocalRepository
import com.andannn.melodify.shared.compose.common.RetainedPresenter
import com.andannn.melodify.shared.compose.common.retainPresenter
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
                val result = repository.getMatchedContentTitle("$query*")
                if (result.isEmpty()) {
                    state = SuggestionsState.NoSuggestion
                } else {
                    state =
                        SuggestionsState.SuggestionLoaded(
                            matched = result,
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
    sealed interface Suggestion : SuggestionsState

    /**
     * Loading search suggestions.
     */
    data object LoadingSuggestion : Suggestion

    /**
     * No search suggestions.
     */
    data object NoSuggestion : Suggestion

    /**
     * Showing search suggestions.
     */
    data class SuggestionLoaded(
        val suggestions: Map<MediaType, List<MatchedContentTitle>>,
    ) : Suggestion {
        constructor(
            matched: List<MatchedContentTitle>,
        ) : this(
            matched.groupBy { it.type },
        )
    }
}
