package com.andannn.melodify.ui.components.librarycontentlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.AudioItemModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import melodify.ui.common.generated.resources.Res
import melodify.ui.common.generated.resources.album_page_title
import melodify.ui.common.generated.resources.artist_page_title
import melodify.ui.common.generated.resources.audio_page_title
import melodify.ui.common.generated.resources.favorite
import melodify.ui.common.generated.resources.genre_title
import melodify.ui.common.generated.resources.playlist_page_title
import org.jetbrains.compose.resources.getString
import org.koin.mp.KoinPlatform.getKoin

@Composable
internal fun rememberLibraryContentListState(
    dataSource: LibraryDataSource,
    scope: CoroutineScope = rememberCoroutineScope(),
    repository: Repository = getKoin().get(),
) = remember(
    dataSource,
    scope,
    repository
) {
    LibraryContentListStateHolder(
        scope = scope,
        repository = repository,
        dataSource = dataSource,
    )
}

internal class LibraryContentListStateHolder(
    scope: CoroutineScope,
    private val repository: Repository,
    private val dataSource: LibraryDataSource,
) {
    val contentList = with(dataSource) {
        repository.content()
    }.stateIn(scope, started = SharingStarted.Eagerly, initialValue = emptyList())

    val title = flow {
        emit(dataSource.getTitle())
    }.stateIn(scope, started = SharingStarted.Eagerly, initialValue = "")

    private suspend fun LibraryDataSource.getTitle() = when (this) {
        LibraryDataSource.AllAlbum -> getString(Res.string.album_page_title)
        LibraryDataSource.AllArtist -> getString(Res.string.artist_page_title)
        LibraryDataSource.AllGenre -> getString(Res.string.genre_title)
        LibraryDataSource.AllPlaylist -> getString(Res.string.playlist_page_title)
        LibraryDataSource.AllSong -> getString(Res.string.audio_page_title)
        LibraryDataSource.Favorite -> getString(Res.string.favorite)
        is LibraryDataSource.AlbumDetail -> repository.mediaContentRepository.getAlbumByAlbumId(id)?.name ?: ""
        is LibraryDataSource.ArtistDetail -> repository.mediaContentRepository.getArtistByArtistId(id)?.name ?: ""
        is LibraryDataSource.GenreDetail -> repository.mediaContentRepository.getGenreByGenreId(id)?.name ?: ""
        is LibraryDataSource.PlayListDetail -> repository.playListRepository.getPlayListById(id.toLong())?.name ?: ""
    }

    fun playMusic(audioItemModel: AudioItemModel) {
        val mediaItems = contentList.value.filterIsInstance<AudioItemModel>()

       repository.mediaControllerRepository.playMediaList(
            mediaItems.toList(),
            mediaItems.indexOf(audioItemModel)
        )
    }
}