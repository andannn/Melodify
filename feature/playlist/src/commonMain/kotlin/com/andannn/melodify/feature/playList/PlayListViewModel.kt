package com.andannn.melodify.feature.playList

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andannn.melodify.feature.common.GlobalUiController
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.MediaListSource
import com.andannn.melodify.core.data.MediaControllerRepository
import com.andannn.melodify.core.data.PlayerStateMonitoryRepository
import com.andannn.melodify.core.data.MediaContentObserverRepository
import com.andannn.melodify.core.data.MediaContentRepository
import com.andannn.melodify.feature.common.drawer.SheetModel
import com.andannn.melodify.feature.playList.navigation.ID
import com.andannn.melodify.feature.playList.navigation.SOURCE
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface PlayListEvent {
    data class OnStartPlayAtIndex(
        val index: Int,
    ) : PlayListEvent

    data object OnPlayAllButtonClick : PlayListEvent

    data object OnShuffleButtonClick : PlayListEvent

    data class OnOptionClick(
        val mediaItem: MediaItemModel,
    ) : PlayListEvent

    data object OnHeaderOptionClick : PlayListEvent
}

@OptIn(ExperimentalCoroutinesApi::class)
class PlayListViewModel(
    savedStateHandle: SavedStateHandle,
    private val contentObserverRepository: MediaContentObserverRepository,
    playerStateMonitoryRepository: PlayerStateMonitoryRepository,
    private val mediaControllerRepository: MediaControllerRepository,
    private val mediaContentRepository: MediaContentRepository,
    private val globalUiController: GlobalUiController,
) : ViewModel() {
    private val id =
        savedStateHandle.get<String>(ID) ?: ""

    val mediaListSource =
        MediaListSource.fromString(savedStateHandle.get<String>(SOURCE) ?: "")
            ?: MediaListSource.ALBUM

    private val contentUri
        get() = with(contentObserverRepository) {
            when (mediaListSource) {
                MediaListSource.ALBUM -> getAlbumUri(id.toLong())
                MediaListSource.ARTIST -> getArtistUri(id.toLong())
                MediaListSource.GENRE -> getGenreUri(id.toLong())
            }
        }

    private val playListContentFlow =
        contentObserverRepository.getContentChangedEventFlow(contentUri)
            .distinctUntilChanged()
            .mapLatest { _ ->
                getPlayListContent()
            }

    private val playingItemFlow = playerStateMonitoryRepository.playingMediaStateFlow

    val state = combine(
        playListContentFlow,
        playingItemFlow,
    ) { content, playingItem ->
        PlayListUiState(
            headerInfoItem = content.headerInfoItem,
            audioList = content.audioList,
            playingMediaItem = playingItem,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), PlayListUiState())

    private suspend fun getPlayListContent(): PlayListContent {
        return when (mediaListSource) {
            MediaListSource.ALBUM -> {
                val albumItem =
                    mediaContentRepository.getAlbumByAlbumId(id.toLong())
                val playableItems = mediaContentRepository.getAudiosOfAlbum(id.toLong())
                    .sortedBy { it.cdTrackNumber }
                PlayListContent(
                    headerInfoItem = albumItem,
                    audioList = playableItems.toImmutableList(),
                )
            }

            MediaListSource.ARTIST -> {
                val headerItem =
                    mediaContentRepository.getArtistByArtistId(id.toLong())

                val playableItems = mediaContentRepository.getAudiosOfArtist(id.toLong())
                PlayListContent(
                    headerInfoItem = headerItem,
                    audioList = playableItems.toImmutableList(),
                )
            }

            MediaListSource.GENRE -> {
                val headerItem = mediaContentRepository.getGenreByGenreId(id.toLong())

                val playableItems = mediaContentRepository.getAudiosOfGenre(id.toLong())
                PlayListContent(
                    headerInfoItem = headerItem,
                    audioList = playableItems.toImmutableList(),
                )

            }
        }
    }

    fun onEvent(event: PlayListEvent) {
        when (event) {
            is PlayListEvent.OnStartPlayAtIndex -> {
                setPlayListAndStartIndex(state.value.audioList, event.index)
            }

            is PlayListEvent.OnPlayAllButtonClick -> {
                setPlayListAndStartIndex(state.value.audioList, 0)
            }

            is PlayListEvent.OnShuffleButtonClick -> {
                setPlayListAndStartIndex(state.value.audioList, 0, isShuffle = true)
            }

            is PlayListEvent.OnOptionClick -> {
                viewModelScope.launch {
                    globalUiController.updateBottomSheet(
                        SheetModel.MediaOptionSheet.fromMediaModel(event.mediaItem)
                    )
                }
            }

            PlayListEvent.OnHeaderOptionClick -> {
                viewModelScope.launch {
                    state.value.headerInfoItem?.let {
                        globalUiController.updateBottomSheet(
                            SheetModel.MediaOptionSheet.fromMediaModel(it)
                        )
                    }
                }
            }
        }
    }

    private fun setPlayListAndStartIndex(
        mediaItems: List<AudioItemModel>,
        index: Int,
        isShuffle: Boolean = false,
    ) {
        mediaControllerRepository.playMediaList(mediaItems, index)
    }
}

private data class PlayListContent(
    val headerInfoItem: MediaItemModel? = null,
    val audioList: ImmutableList<AudioItemModel> = emptyList<AudioItemModel>().toImmutableList(),
)

data class PlayListUiState(
    val headerInfoItem: MediaItemModel? = null,
    val audioList: ImmutableList<AudioItemModel> = emptyList<AudioItemModel>().toImmutableList(),
    val playingMediaItem: AudioItemModel? = null,
)
