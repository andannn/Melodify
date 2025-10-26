/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.librarycontentlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.browsable
import com.andannn.melodify.ui.components.common.LocalRepository
import com.andannn.melodify.ui.components.common.newLibraryContentListScreen
import com.andannn.melodify.ui.components.library.util.asDataSource
import com.slack.circuit.retained.collectAsRetainedState
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import melodify.composeapp.generated.resources.Res
import melodify.composeapp.generated.resources.album_page_title
import melodify.composeapp.generated.resources.artist_page_title
import melodify.composeapp.generated.resources.audio_page_title
import melodify.composeapp.generated.resources.favorite
import melodify.composeapp.generated.resources.genre_title
import melodify.composeapp.generated.resources.playlist_page_title
import org.jetbrains.compose.resources.getString

@Composable
fun rememberLibraryContentPresenter(
    dataSource: LibraryDataSource,
    navigator: Navigator,
    repository: Repository = LocalRepository.current,
) = remember(repository, navigator, dataSource) {
    LibraryContentPresenter(repository, navigator, dataSource)
}

class LibraryContentPresenter(
    private val repository: Repository,
    private val navigator: Navigator,
    private val dataSource: LibraryDataSource,
) : Presenter<LibraryContentState> {
    @Composable
    override fun present(): LibraryContentState {
        val contentList by with(repository) { dataSource.content() }.collectAsRetainedState(
            emptyList(),
        )
        val title by produceRetainedState("") {
            value = with(repository) { dataSource.getTitle() }
        }
        return LibraryContentState(
            contentList,
            title,
        ) { eventSink ->
            when (eventSink) {
                is LibraryContentUiEvent.OnItemClick -> {
                    if (eventSink.item.browsable) {
                        navigator.goTo(newLibraryContentListScreen(eventSink.item.asDataSource()))
                    } else {
                        playMusic(eventSink.item as AudioItemModel, contentList)
                    }
                }

                LibraryContentUiEvent.OnBack -> navigator.pop()
            }
        }
    }

    private fun playMusic(
        audioItemModel: AudioItemModel,
        contentList: List<MediaItemModel>,
    ) {
        val mediaItems = contentList.filterIsInstance<AudioItemModel>()

        repository.mediaControllerRepository.playMediaList(
            mediaItems.toList(),
            mediaItems.indexOf(audioItemModel),
        )
    }
}

context(repository: Repository)
private suspend fun LibraryDataSource.getTitle() =
    when (this) {
        LibraryDataSource.AllAlbum -> getString(Res.string.album_page_title)
        LibraryDataSource.AllArtist -> getString(Res.string.artist_page_title)
        LibraryDataSource.AllGenre -> getString(Res.string.genre_title)
        LibraryDataSource.AllPlaylist -> getString(Res.string.playlist_page_title)
        LibraryDataSource.AllSong -> getString(Res.string.audio_page_title)
        LibraryDataSource.Favorite -> getString(Res.string.favorite)
        is LibraryDataSource.AlbumDetail ->
            repository.mediaContentRepository.getAlbumByAlbumId(id)?.name
                ?: ""

        is LibraryDataSource.ArtistDetail ->
            repository.mediaContentRepository.getArtistByArtistId(id)?.name
                ?: ""

        is LibraryDataSource.GenreDetail ->
            repository.mediaContentRepository.getGenreByGenreId(id)?.name
                ?: ""

        is LibraryDataSource.PlayListDetail ->
            repository.playListRepository.getPlayListById(id.toLong())?.name
                ?: ""
    }

data class LibraryContentState(
    val contentList: List<MediaItemModel> = emptyList(),
    val title: String = "",
    val eventSink: (LibraryContentUiEvent) -> Unit = {},
) : CircuitUiState

sealed interface LibraryContentUiEvent {
    data class OnItemClick(
        val item: MediaItemModel,
    ) : LibraryContentUiEvent

    data object OnBack : LibraryContentUiEvent
}
