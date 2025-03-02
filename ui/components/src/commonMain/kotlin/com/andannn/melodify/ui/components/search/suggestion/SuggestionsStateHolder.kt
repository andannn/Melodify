package com.andannn.melodify.ui.components.search.suggestion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.repository.MediaContentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin

@Composable
internal fun rememberSuggestionsStateHolder(
    query: String,
    repository: Repository = getKoin().get(),
    scope: CoroutineScope = rememberCoroutineScope()
) = remember(
    query,
    repository,
    scope
) {
    SuggestionsStateHolder(
        query = query,
        repository = repository.mediaContentRepository,
        scope = scope
    )
}

internal class SuggestionsStateHolder(
    query: String,
    repository: MediaContentRepository,
    scope: CoroutineScope,
) {
    private val _state: MutableStateFlow<SuggestionsState>
    val state get() = _state.asStateFlow()

    init {
        val initialState =
            if (query.isEmpty()) SuggestionsState.LoadingHistory else SuggestionsState.LoadingSuggestion

        _state = MutableStateFlow(initialState)

        if (initialState is SuggestionsState.LoadingHistory) {
            scope.launch {
                // TODO load history search suggestion
                _state.value = SuggestionsState.HistoryLoaded(listOf("A", "B", "C"))
            }
        } else {
            scope.launch {
                val result = repository.searchContent("$query*")
                if (result.isEmpty()) {
                    _state.value = SuggestionsState.NoSuggestion
                } else {
                    // TODO: use string similarity to find best matched items
                    val bestMatchedItems = result.filter {
                        it.name == query
                    }
                    _state.value = SuggestionsState.SuggestionLoaded(
                        suggestions = result.map { it.name }.distinct(),
                        bestMatchedItems = bestMatchedItems
                    )
                }
            }
        }

    }
}

internal sealed interface SuggestionsState {
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
        val bestMatchedItems: List<MediaItemModel>
    ) : Suggestion()
}