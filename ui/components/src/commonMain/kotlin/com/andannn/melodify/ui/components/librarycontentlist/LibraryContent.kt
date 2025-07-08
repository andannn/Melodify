package com.andannn.melodify.ui.components.librarycontentlist

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.repository.PlayListRepository.Companion.FAVORITE_PLAY_LIST_ID
import com.andannn.melodify.ui.components.common.MediaItemWithOptionAction

@Composable
fun LibraryContent(
    state: LibraryContentState,
    modifier: Modifier = Modifier,
) {
    LibraryContentList(
        modifier = modifier,
        title = state.title,
        contentList = state.contentList,
        onBackPressed = {
            state.eventSink.invoke(LibraryContentUiEvent.OnBack)
        },
        onItemClick = {
            state.eventSink.invoke(LibraryContentUiEvent.OnItemClick(it))
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryContentList(
    title: String,
    contentList: List<MediaItemModel>,
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
    onItemClick: (MediaItemModel) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                title = {
                    Text(text = title)
                },
            )
        },
    ) {
        LazyColumn(
            modifier = Modifier.padding(it),
            contentPadding = PaddingValues(start = 8.dp, end = 8.dp, bottom = 128.dp),
        ) {
            items(
                items = contentList,
                key = { it.id },
            ) { item ->
                MediaItemWithOptionAction(
                    modifier = Modifier.padding(vertical = 4.dp),
                    mediaItemModel = item,
                    onItemClick = {
                        onItemClick(item)
                    },
                )
            }
        }
    }
}

sealed interface LibraryDataSource {
    data object AllSong : LibraryDataSource

    data object AllArtist : LibraryDataSource

    data object AllAlbum : LibraryDataSource

    data object AllGenre : LibraryDataSource

    data object AllPlaylist : LibraryDataSource

    data object Favorite : LibraryDataSource

    data class ArtistDetail(val id: String) : LibraryDataSource

    data class AlbumDetail(val id: String) : LibraryDataSource

    data class GenreDetail(val id: String) : LibraryDataSource

    data class PlayListDetail(val id: String) : LibraryDataSource

    fun toStringCode() =
        when (this) {
            AllSong -> "AllSong"
            AllArtist -> "AllArtist"
            AllAlbum -> "AllAlbum"
            AllGenre -> "AllGenre"
            AllPlaylist -> "AllPlaylist"
            Favorite -> "Favorite"
            is ArtistDetail -> "ArtistDetail($id)"
            is AlbumDetail -> "AlbumDetail($id)"
            is GenreDetail -> "GenreDetail($id)"
            is PlayListDetail -> "PlayListDetail($id)"
        }

    companion object {
        fun parseFromString(code: String): LibraryDataSource {
            return if (code == "AllSong") {
                AllSong
            } else if (code == "AllArtist") {
                AllArtist
            } else if (code == "AllAlbum") {
                AllAlbum
            } else if (code == "AllGenre") {
                AllGenre
            } else if (code == "AllPlaylist") {
                AllPlaylist
            } else if (code == "Favorite") {
                Favorite
            } else if (code.startsWith("ArtistDetail")) {
                val id = code.substring("ArtistDetail(".length, code.length - 1)
                ArtistDetail(id)
            } else if (code.startsWith("AlbumDetail")) {
                val id = code.substring("AlbumDetail(".length, code.length - 1)
                AlbumDetail(id)
            } else if (code.startsWith("GenreDetail")) {
                val id = code.substring("GenreDetail(".length, code.length - 1)
                GenreDetail(id)
            } else if (code.startsWith("PlayListDetail")) {
                val id = code.substring("PlayListDetail(".length, code.length - 1)
                PlayListDetail(id)
            } else {
                throw IllegalArgumentException("Unknown code: $code")
            }
        }
    }
}

fun LibraryDataSource.content(repository: Repository) =
    when (this) {
        is LibraryDataSource.AlbumDetail -> repository.mediaContentRepository.getAudiosOfAlbumFlow(id)
        LibraryDataSource.AllAlbum -> repository.mediaContentRepository.getAllAlbumsFlow()
        LibraryDataSource.AllArtist -> repository.mediaContentRepository.getAllArtistFlow()
        LibraryDataSource.AllGenre -> repository.mediaContentRepository.getAllGenreFlow()
        LibraryDataSource.AllPlaylist -> repository.playListRepository.getAllPlayListFlow()
        LibraryDataSource.AllSong -> repository.mediaContentRepository.getAllMediaItemsFlow()
        is LibraryDataSource.ArtistDetail -> repository.mediaContentRepository.getAudiosOfArtistFlow(id)
        LibraryDataSource.Favorite ->
            repository.playListRepository.getAudiosOfPlayListFlow(
                FAVORITE_PLAY_LIST_ID,
            )

        is LibraryDataSource.GenreDetail -> repository.mediaContentRepository.getAudiosOfGenreFlow(id)
        is LibraryDataSource.PlayListDetail -> repository.playListRepository.getAudiosOfPlayListFlow(id.toLong())
    }
