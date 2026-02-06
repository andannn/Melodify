/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.components.library.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andannn.melodify.domain.Repository
import com.andannn.melodify.domain.model.AudioItemModel
import com.andannn.melodify.domain.model.MediaItemModel
import com.andannn.melodify.shared.compose.common.LocalNavigationRequestEventSink
import com.andannn.melodify.shared.compose.common.LocalRepository
import com.andannn.melodify.shared.compose.common.NavigationRequest
import com.andannn.melodify.shared.compose.common.NavigationRequestEventSink
import com.andannn.melodify.shared.compose.common.Presenter
import com.andannn.melodify.shared.compose.common.RetainedPresenter
import com.andannn.melodify.shared.compose.common.model.LibraryDataSource
import com.andannn.melodify.shared.compose.common.model.asLibraryDataSource
import com.andannn.melodify.shared.compose.common.model.browseable
import com.andannn.melodify.shared.compose.common.retainPresenter
import com.andannn.melodify.shared.compose.common.stateInRetainedModel
import com.andannn.melodify.shared.compose.popup.LocalPopupHostState
import com.andannn.melodify.shared.compose.usecase.content
import com.andannn.melodify.shared.compose.usecase.item
import com.andannn.melodify.shared.compose.usecase.playMediaItems
import io.github.aakira.napier.Napier
import io.github.andannn.popup.PopupHostState
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import melodify.shared.compose.resource.generated.resources.Res
import melodify.shared.compose.resource.generated.resources.album_page_title
import melodify.shared.compose.resource.generated.resources.artist_page_title
import melodify.shared.compose.resource.generated.resources.audio_page_title
import melodify.shared.compose.resource.generated.resources.genre_title
import melodify.shared.compose.resource.generated.resources.playlist_page_title
import melodify.shared.compose.resource.generated.resources.video_page_title
import org.jetbrains.compose.resources.getString

@Composable
fun retainLibraryDetailPresenter(
    dataSource: LibraryDataSource,
    navigationRequestSink: NavigationRequestEventSink = LocalNavigationRequestEventSink.current,
    repository: Repository = LocalRepository.current,
    popupHostState: PopupHostState = LocalPopupHostState.current,
): Presenter<LibraryContentState> =
    retainPresenter(repository, dataSource, navigationRequestSink, popupHostState) {
        LibraryDetailPresenter(repository, dataSource, navigationRequestSink, popupHostState)
    }

@Stable
data class LibraryContentState(
    val dataSource: LibraryDataSource,
    val mediaItem: MediaItemModel?,
    val contentList: List<MediaItemModel> = emptyList(),
    val title: String = "",
    val eventSink: (LibraryContentEvent) -> Unit = {},
)

sealed interface LibraryContentEvent {
    data class OnRequestPlay(
        val mediaItem: AudioItemModel,
    ) : LibraryContentEvent

    data class OnMediaItemClick(
        val mediaItem: MediaItemModel,
    ) : LibraryContentEvent
}

private const val TAG = "LibraryDetailPresenter"

private class LibraryDetailPresenter(
    private val repository: Repository,
    private val dataSource: LibraryDataSource,
    private val navigationRequestSink: NavigationRequestEventSink,
    private val popupHostState: PopupHostState,
) : RetainedPresenter<LibraryContentState>() {
    private val dataSourceMediaItemFlow =
        with(repository) {
            dataSource
                .item()
                .stateInRetainedModel(
                    initialValue = null,
                )
        }

    private val contentListFlow =
        with(repository) {
            dataSource
                .content()
                .stateInRetainedModel(
                    initialValue = emptyList(),
                )
        }

    private var title by mutableStateOf("")

    init {
        retainedScope.launch {
            title = with(repository) { dataSource.getTitle() }
        }
    }

    @Composable
    override fun present(): LibraryContentState {
        val dataSourceMediaItem by dataSourceMediaItemFlow.collectAsStateWithLifecycle()
        val contentList by contentListFlow.collectAsStateWithLifecycle()

        return LibraryContentState(
            dataSource = dataSource,
            mediaItem = dataSourceMediaItem,
            contentList = contentList,
            title = title,
        ) { event ->
            when (event) {
                is LibraryContentEvent.OnRequestPlay -> {
                    playMedia(event.mediaItem, contentList)
                }

                is LibraryContentEvent.OnMediaItemClick -> {
                    if (dataSource.browseable()) {
                        Napier.d(tag = TAG) { "request navigate to ${event.mediaItem.asLibraryDataSource()}" }
                        retainedScope.launch {
                            navigationRequestSink.onRequestNavigate(
                                NavigationRequest.GoToLibraryDetail(event.mediaItem.asLibraryDataSource()),
                            )
                        }
                    } else {
                        playMedia(event.mediaItem, contentList)
                    }
                }
            }
        }
    }

    private fun playMedia(
        audioItemModel: MediaItemModel,
        contentList: List<MediaItemModel>,
    ) {
        retainedScope.launch {
            context(
                repository,
                popupHostState,
            ) {
                playMediaItems(
                    audioItemModel,
                    contentList.toList(),
                )
            }
        }
    }
}

context(repository: Repository)
private suspend fun LibraryDataSource.getTitle(): String =
    when (this) {
        LibraryDataSource.AllAlbum -> {
            getString(Res.string.album_page_title)
        }

        LibraryDataSource.AllArtist -> {
            getString(Res.string.artist_page_title)
        }

        LibraryDataSource.AllGenre -> {
            getString(Res.string.genre_title)
        }

        LibraryDataSource.AllPlaylist -> {
            getString(Res.string.playlist_page_title)
        }

        LibraryDataSource.AllSong -> {
            getString(Res.string.audio_page_title)
        }

        LibraryDataSource.AllVideo -> {
            getString(Res.string.video_page_title)
        }

        is LibraryDataSource.AlbumDetail -> {
            repository.getAlbumByAlbumId(id.toLong())?.name
                ?: ""
        }

        is LibraryDataSource.ArtistDetail -> {
            repository.getArtistByArtistId(id.toLong())?.name
                ?: ""
        }

        is LibraryDataSource.GenreDetail -> {
            repository.getGenreByGenreId(id.toLong())?.name
                ?: ""
        }

        is LibraryDataSource.PlayListDetail -> {
            repository.getPlayListById(id.toLong())?.name
                ?: ""
        }
    }
