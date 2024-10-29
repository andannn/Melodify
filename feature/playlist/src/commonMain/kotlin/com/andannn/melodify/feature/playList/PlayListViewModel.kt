package com.andannn.melodify.feature.playList

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.MediaListSource
import com.andannn.melodify.core.data.MediaControllerRepository
import com.andannn.melodify.core.data.PlayerStateMonitoryRepository
import com.andannn.melodify.core.data.MediaContentRepository
import com.andannn.melodify.feature.drawer.DrawerController
import com.andannn.melodify.feature.drawer.DrawerEvent
import com.andannn.melodify.feature.drawer.model.SheetModel
import com.andannn.melodify.feature.playList.navigation.ID
import com.andannn.melodify.feature.playList.navigation.SOURCE
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
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

class PlayListViewModel(
    savedStateHandle: SavedStateHandle,
    playerStateMonitoryRepository: PlayerStateMonitoryRepository,
    private val mediaControllerRepository: MediaControllerRepository,
    private val mediaContentRepository: MediaContentRepository,
    private val drawerController: DrawerController,
) : ViewModel() {
    private val id =
        savedStateHandle.get<String>(ID) ?: ""

    val mediaListSource =
        MediaListSource.fromString(savedStateHandle.get<String>(SOURCE) ?: "")
            ?: MediaListSource.ALBUM

    private val playingItemFlow = playerStateMonitoryRepository.playingMediaStateFlow

    val state = combine(
        getPlayListContent(),
        playingItemFlow,
    ) { content, playingItem ->
        PlayListUiState(
            headerInfoItem = content.headerInfoItem,
            audioList = content.audioList,
            playingMediaItem = playingItem,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), PlayListUiState())

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getPlayListContent() = with(mediaContentRepository) {
        when (mediaListSource) {
            MediaListSource.ALBUM -> {
                getAudiosOfAlbumFlow(id).mapLatest { audioList ->
                    PlayListContent(
                        headerInfoItem = getAlbumByAlbumId(id),
                        audioList = audioList.sortedBy { it.cdTrackNumber }.toImmutableList(),
                    )
                }
            }

            MediaListSource.ARTIST -> {
                getAudiosOfArtistFlow(id).mapLatest { audioList ->
                    PlayListContent(
                        headerInfoItem = getArtistByArtistId(id),
                        audioList = audioList.sortedBy { it.name }.toImmutableList(),
                    )
                }
            }

            MediaListSource.GENRE -> {
                getAudiosOfGenreFlow(id).mapLatest { audioList ->
                    PlayListContent(
                        headerInfoItem = getGenreByGenreId(id),
                        audioList = audioList.sortedBy { it.name }.toImmutableList(),
                    )
                }
            }

            MediaListSource.PLAY_LIST -> {
                getAudiosOfPlayListFlow(id.toLong()).mapLatest { audioList ->
                    PlayListContent(
                        headerInfoItem = getPlayListById(id.toLong()),
                        audioList = audioList.toImmutableList(),
                    )
                }
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
                    drawerController.onEvent(
                        DrawerEvent.OnShowBottomDrawer(
                            SheetModel.MediaOptionSheet.fromMediaModel(event.mediaItem)
                        )
                    )
                }
            }

            PlayListEvent.OnHeaderOptionClick -> {
                viewModelScope.launch {
                    state.value.headerInfoItem?.let {
                        drawerController.onEvent(
                            DrawerEvent.OnShowBottomDrawer(
                                SheetModel.MediaOptionSheet.fromMediaModel(it)
                            )
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
