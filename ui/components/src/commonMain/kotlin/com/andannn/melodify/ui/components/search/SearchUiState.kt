package com.andannn.melodify.ui.components.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.repository.MediaContentRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
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
    val resultListFlow = searchTextFlow.flatMapLatest { text ->
        flow {
            emit(SearchState.Searching)
            if (isValid(text)) {
                val result = contentLibrary.searchContent(text)
                if (result.isEmpty()) {
                    emit(SearchState.NoObject)
                } else {
                    emit(toResult(result))
                }
            } else {
                emit(SearchState.Result(emptyList()))
            }
        }
    }.stateIn(scope, Eagerly, SearchState.Init)

    fun onConfirmSearch(text: String) {
        Napier.d(tag = TAG) { "onConfirmSearch: $text" }
        searchTextFlow.value = "$text*"
    }

    private fun isValid(text: String) = text.isNotBlank()

    private fun toResult(result: List<MediaItemModel>) = SearchState.Result(
        albums = result.filterIsInstance<AlbumItemModel>(),
        artists = result.filterIsInstance<ArtistItemModel>(),
        audios = result.filterIsInstance<AudioItemModel>()
    )
}

sealed interface SearchState {
    data object Init: SearchState

    data object Searching: SearchState

    data class Result(
        val albums: List<AlbumItemModel> = emptyList(),
        val artists: List<ArtistItemModel> = emptyList(),
        val audios: List<AudioItemModel> = emptyList(),
    ): SearchState

    data object NoObject: SearchState
}