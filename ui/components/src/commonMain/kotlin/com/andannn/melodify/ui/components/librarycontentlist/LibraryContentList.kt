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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.browsable
import com.andannn.melodify.core.data.repository.PlayListRepository.Companion.FAVORITE_PLAY_LIST_ID
import com.andannn.melodify.ui.components.library.util.asDataSource
import com.andannn.melodify.ui.components.search.searchedItem.MediaItemWithOptionAction
import kotlinx.coroutines.flow.Flow

@Composable
fun LibraryContentListView(
    modifier: Modifier = Modifier,
    dataSource: LibraryDataSource,
    onNavigateToLibraryContentList: (LibraryDataSource) -> Unit = {},
    onBackPressed: () -> Unit = {},
) {
    val stateHolder = rememberLibraryContentListState(dataSource = dataSource)
    val contentList by stateHolder.contentList.collectAsState()
    val title by stateHolder.title.collectAsState()

    LibraryContentList(
        modifier = modifier,
        title = title,
        contentList = contentList,
        onBackPressed = onBackPressed,
        onItemClick = {
            if (it.browsable) {
                onNavigateToLibraryContentList(it.asDataSource())
            } else {
            }
        }
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
        }
    ) {
        LazyColumn(
            modifier = Modifier.padding(it),
            contentPadding = PaddingValues(horizontal = 8.dp),
        ) {
            items(
                items = contentList,
                key = { it.id }
            ) { item ->
                MediaItemWithOptionAction(
                    modifier = Modifier.padding(vertical = 4.dp),
                    mediaItemModel = item,
                    onItemClick = {
                        onItemClick(item)
                    }
                )
            }
        }
    }
}

sealed interface LibraryDataSource {
    fun Repository.content(): Flow<List<MediaItemModel>>

    data object AllSong : LibraryDataSource {
        override fun Repository.content(): Flow<List<MediaItemModel>> =
            mediaContentRepository.getAllMediaItemsFlow()
    }

    data object AllArtist : LibraryDataSource {
        override fun Repository.content(): Flow<List<MediaItemModel>> =
            mediaContentRepository.getAllArtistFlow()
    }

    data object AllAlbum : LibraryDataSource {
        override fun Repository.content(): Flow<List<MediaItemModel>> =
            mediaContentRepository.getAllAlbumsFlow()
    }

    data object AllGenre : LibraryDataSource {
        override fun Repository.content(): Flow<List<MediaItemModel>> =
            mediaContentRepository.getAllGenreFlow()
    }

    data object AllPlaylist : LibraryDataSource {
        override fun Repository.content(): Flow<List<MediaItemModel>> =
            playListRepository.getAllPlayListFlow()
    }

    data object Favorite : LibraryDataSource {
        override fun Repository.content(): Flow<List<MediaItemModel>> =
            playListRepository.getAudiosOfPlayListFlow(FAVORITE_PLAY_LIST_ID)
    }

    data class ArtistDetail(val id: String) : LibraryDataSource {
        override fun Repository.content(): Flow<List<MediaItemModel>> =
            mediaContentRepository.getAudiosOfArtistFlow(id)
    }

    data class AlbumDetail(val id: String) : LibraryDataSource {
        override fun Repository.content(): Flow<List<MediaItemModel>> =
            mediaContentRepository.getAudiosOfAlbumFlow(id)
    }

    data class GenreDetail(val id: String) : LibraryDataSource {
        override fun Repository.content(): Flow<List<MediaItemModel>> =
            mediaContentRepository.getAudiosOfGenreFlow(id)
    }

    data class PlayListDetail(val id: String) : LibraryDataSource {
        override fun Repository.content(): Flow<List<MediaItemModel>> =
            playListRepository.getAudiosOfPlayListFlow(id.toLong())
    }

    fun toStringCode() = when (this) {
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
