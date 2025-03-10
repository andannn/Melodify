package com.andannn.melodify.ui.components.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.repository.MediaContentRepository
import com.andannn.melodify.core.data.repository.MediaControllerRepository
import com.andannn.melodify.core.data.repository.PlayerStateMonitoryRepository
import com.andannn.melodify.core.data.repository.UserPreferenceRepository
import com.andannn.melodify.ui.components.common.newLibraryContentListScreen
import com.andannn.melodify.ui.components.librarycontentlist.LibraryDataSource
import com.andannn.melodify.ui.components.playcontrol.LocalPlayerUiController
import com.andannn.melodify.ui.components.playcontrol.PlayerUiController
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun rememberSearchUiPresenter(
    navigator: Navigator,
    repository: Repository = getKoin().get(),
    playerUiController: PlayerUiController = LocalPlayerUiController.current,
) = remember(
    repository,
    playerUiController,
) {
    SearchUiPresenter(
        navigator,
        playerUiController,
        repository.mediaContentRepository,
        repository.userPreferenceRepository,
        repository.mediaControllerRepository,
        repository.playerStateMonitoryRepository,
    )
}

class SearchUiPresenter(
    private val navigator: Navigator,
    private val playerUiController: PlayerUiController,
    private val contentLibrary: MediaContentRepository,
    private val userPreferenceRepository: UserPreferenceRepository,
    private val mediaControllerRepository: MediaControllerRepository,
    private val playerStateMonitoryRepository: PlayerStateMonitoryRepository
) : Presenter<SearchUiState> {

    @Composable
    override fun present(): SearchUiState {
        val scope = rememberCoroutineScope()

        var searchText by rememberSaveable {
            mutableStateOf("")
        }

        var searchResult by remember {
            mutableStateOf<SearchState>(SearchState.Init)
        }

        LaunchedEffect(searchText) {
            searchResult = SearchState.Searching

            searchResult = if (isValid(searchText)) {
                val result = contentLibrary.searchContent(searchText)
                if (result.isEmpty()) {
                    SearchState.NoObject
                } else {
                    toResult(result)
                }
            } else {
                SearchState.Result(emptyList())
            }
        }

        return SearchUiState(
            searchState = searchResult
        ) { eventSink ->
            when (eventSink) {
                is SearchUiEvent.OnConfirmSearch -> {
                    val text = eventSink.text
                    searchText = "$text*"

                    scope.launch {
                        userPreferenceRepository.addSearchHistory(text)
                    }

                }

                is SearchUiEvent.OnPlayAudio -> onPlayAudio(scope, eventSink.audioItemModel)
                SearchUiEvent.Back -> navigator.pop()
                is SearchUiEvent.OnNavigateToLibraryContentList -> navigator.goTo(
                    newLibraryContentListScreen(eventSink.source)
                )
            }
        }
    }

    private fun onPlayAudio(scope: CoroutineScope, audioItemModel: AudioItemModel) {
        scope.launch {
            val isQueueEmpty = playerStateMonitoryRepository.getPlayListQueue().isEmpty()
            if (!isQueueEmpty) {
                // add audio item to queue and play this audio item
                val currentIndex = playerStateMonitoryRepository.getPlayingIndexInQueue()
                val newToPlayIndex = currentIndex + 1
                mediaControllerRepository.addMediaItems(newToPlayIndex, listOf(audioItemModel))
                mediaControllerRepository.seekMediaItem(newToPlayIndex)
                mediaControllerRepository.play()
            } else {
                // play audio item
                mediaControllerRepository.playMediaList(listOf(audioItemModel))
            }

            playerUiController.expandPlayer()
        }
    }

    private fun isValid(text: String) = text.isNotBlank()

    private fun toResult(result: List<MediaItemModel>) = SearchState.Result(
        albums = result.filterIsInstance<AlbumItemModel>(),
        artists = result.filterIsInstance<ArtistItemModel>(),
        audios = result.filterIsInstance<AudioItemModel>()
    )
}

data class SearchUiState(
    val searchState: SearchState = SearchState.Init,
    val eventSink: (SearchUiEvent) -> Unit = {}
) : CircuitUiState

sealed interface SearchUiEvent {
    data class OnPlayAudio(val audioItemModel: AudioItemModel) : SearchUiEvent

    data class OnConfirmSearch(val text: String) : SearchUiEvent

    data class OnNavigateToLibraryContentList(val source: LibraryDataSource) : SearchUiEvent

    data object Back : SearchUiEvent
}

sealed interface SearchState {
    data object Init : SearchState

    data object Searching : SearchState

    data class Result(
        val albums: List<AlbumItemModel> = emptyList(),
        val artists: List<ArtistItemModel> = emptyList(),
        val audios: List<AudioItemModel> = emptyList(),
    ) : SearchState

    data object NoObject : SearchState
}