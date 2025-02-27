package com.andannn.melodify.ui.components.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.repository.MediaContentRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun rememberSearchUiState(
    scope: CoroutineScope = rememberCoroutineScope(),
    repository: Repository = getKoin().get(),
) = remember(
    scope,
    repository,
) {
    SearchUiStateHolder(
        scope,
        repository.mediaContentRepository
    )
}

private const val TAG = "SearchUiState"

class SearchUiStateHolder(
    scope: CoroutineScope,
    private val contentLibrary: MediaContentRepository
) {
    private val searchTextFlow = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    private val resultListFlow = searchTextFlow.mapLatest { text ->
        if (isValid(text)) {
            contentLibrary.searchContent(text)
        } else {
            return@mapLatest emptyList()
        }
    }

    val searchedResult = mutableStateListOf<MediaItemModel>()

    init {
        scope.launch {
            resultListFlow.collectLatest {
                searchedResult.addAll(it)
            }
        }
    }

    private fun isValid(text: String) = text.isNotBlank()

    fun onConfirmSearch(text: String) {
        Napier.d(tag = TAG) { "onConfirmSearch: $text" }
        searchTextFlow.value = "$text*"
    }
}