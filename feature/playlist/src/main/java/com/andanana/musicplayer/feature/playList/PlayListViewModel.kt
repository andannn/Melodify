package com.andanana.musicplayer.feature.playList

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andanana.musicplayer.core.data.repository.LocalMusicRepository
import com.andanana.musicplayer.core.database.usecases.PlayListUseCases
import com.andanana.musicplayer.core.model.MusicInfo
import com.andanana.musicplayer.core.model.RequestType
import com.andanana.musicplayer.core.model.RequestType.Companion.toRequestType
import com.andanana.musicplayer.core.model.RequestType.Companion.toUri
import com.andanana.musicplayer.core.player.repository.PlayerRepository
import com.andanana.musicplayer.feature.playList.navigation.requestUriLastSegmentArg
import com.andanana.musicplayer.feature.playList.navigation.requestUriTypeArg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "PlayListViewModel"

@HiltViewModel
class PlayListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: LocalMusicRepository,
    private val playerRepository: PlayerRepository,
    private val useCases: PlayListUseCases
) : ViewModel(), PlayerRepository by playerRepository {
    private val requestTypeFlow =
        savedStateHandle.getStateFlow(requestUriTypeArg, RequestType.ARTIST_REQUEST)
    private val requestUriLastSegmentFlow =
        savedStateHandle.getStateFlow(requestUriLastSegmentArg, "")

    private val requestUri = combine(
        requestTypeFlow,
        requestUriLastSegmentFlow
    ) { type, lastSegment ->
        type.toUri(lastSegment)
    }

    private val _playListUiStateFlow = MutableStateFlow<PlayListUiState>(PlayListUiState.Loading)
    val playListUiStateFlow = _playListUiStateFlow.asStateFlow()

    private val playListReadyState
        get() = _playListUiStateFlow.value as? PlayListUiState.Ready

    init {
        viewModelScope.launch {
            val uri = requestTypeFlow.value.toUri(requestUriLastSegmentFlow.value)
            when (uri.toRequestType()) {
                RequestType.ALBUM_REQUEST -> {
                    val info = repository.getAlbumInfoById(
                        id = uri.lastPathSegment?.toLong() ?: 0L
                    )
                    val title = info.title
                    val artCoverUri = info.albumUri.toString()
                    val trackCount = info.trackCount
                    val musicItems = repository.getMusicInfoByAlbumId(
                        id = uri.lastPathSegment?.toLong() ?: 0L
                    ).sortedBy { it.cdTrackNumber }
                    _playListUiStateFlow.value = PlayListUiState.Ready(
                        title = title,
                        contentUri = uri,
                        type = uri.toRequestType()!!,
                        artCoverUri = artCoverUri,
                        trackCount = trackCount,
                        musicItems = musicItems
                    )
                }
                RequestType.ARTIST_REQUEST -> {
                    val info = repository.getArtistInfoById(
                        id = uri.lastPathSegment?.toLong() ?: 0L
                    )
                    val title = info.name
                    val artCoverUri = info.artistCoverUri.toString()
                    val trackCount = info.trackCount
                    val musicItems = repository.getMusicInfoByArtistId(
                        id = uri.lastPathSegment?.toLong() ?: 0L
                    )
                    _playListUiStateFlow.value = PlayListUiState.Ready(
                        title = title,
                        contentUri = uri,
                        type = uri.toRequestType()!!,
                        artCoverUri = artCoverUri,
                        trackCount = trackCount,
                        musicItems = musicItems
                    )
                }
                RequestType.PLAYLIST_REQUEST -> {
                    val playListId = uri.lastPathSegment?.toLong() ?: 0L

                    val playList = useCases.getPlayListByPlayListId.invoke(
                        playListId = playListId
                    )
                    val title = playList.name
                    val artCoverUri = ""
                    _playListUiStateFlow.value = PlayListUiState.Ready(
                        title = title,
                        contentUri = uri,
                        type = uri.toRequestType()!!,
                        artCoverUri = artCoverUri,
                        trackCount = 0,
                        musicItems = emptyList()
                    )

                    viewModelScope.launch {
                        useCases.getMusicInPlayList.invoke(
                            playListId = playListId
                        )
                            .map { it.sortedByDescending { it.musicAddedDate } }
                            .map { ids ->
                                ids.map {
                                    repository.getMusicInfoById(
                                        id = it.music.mediaStoreId
                                    ) ?: MusicInfo(contentUri = Uri.parse(""))
                                }
                            }
                            .collect { infos ->
                                _playListUiStateFlow.value = playListReadyState?.copy(
                                    trackCount = infos.size,
                                    musicItems = infos
                                ) ?: return@collect
                            }
                    }
                }
                else -> error("Invalid type")
            }

            playerRepository.observePlayingUri().collect { playingUri ->
                _playListUiStateFlow.update {
                    playListReadyState?.copy(
                        interactingUri = playingUri
                    ) ?: return@collect
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            _playListUiStateFlow.collect {
                Log.d(TAG, ":_playListUiStateFlow $it")
            }
        }
    }
}

sealed interface PlayListUiState {
    object Loading : PlayListUiState
    data class Ready(
        val title: String = "",
        val type: RequestType = RequestType.PLAYLIST_REQUEST,
        val artCoverUri: String = "",
        val trackCount: Int = 0,
        val musicItems: List<MusicInfo> = emptyList(),
        val interactingUri: Uri? = null,
        val contentUri: Uri = Uri.EMPTY
    ) : PlayListUiState
}
