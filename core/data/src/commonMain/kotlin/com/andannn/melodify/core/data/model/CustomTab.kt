package com.andannn.melodify.core.data.model

import com.andannn.melodify.core.data.MediaContentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

@Serializable
sealed interface CustomTab {
    fun MediaContentRepository.contentFlow(): Flow<List<MediaItemModel>>

    @Serializable
    data object AllMusic : CustomTab {
        override fun MediaContentRepository.contentFlow() = getAllMediaItemsFlow()
    }

    @Serializable
    data object AllAlbum : CustomTab {
        override fun MediaContentRepository.contentFlow() = getAllAlbumsFlow()
    }

    @Serializable
    data object AllArtist : CustomTab {
        override fun MediaContentRepository.contentFlow() = getAllArtistFlow()
    }

    @Serializable
    data object AllGenre : CustomTab {
        override fun MediaContentRepository.contentFlow() = getAllGenreFlow()
    }

    @Serializable
    data object AllPlayList : CustomTab {
        override fun MediaContentRepository.contentFlow() = getAllPlayListFlow()
    }

    @Serializable
    data class AlbumDetail(val albumId: String, val label: String) : CustomTab {
        override fun MediaContentRepository.contentFlow() = getAudiosOfAlbumFlow(albumId)
    }

    @Serializable
    data class ArtistDetail(val artistId: String, val label: String) : CustomTab {
        override fun MediaContentRepository.contentFlow() = getAudiosOfArtistFlow(artistId)
    }

    @Serializable
    data class GenreDetail(val genreId: String, val label: String) : CustomTab {
        override fun MediaContentRepository.contentFlow() = getAudiosOfGenreFlow(genreId)
    }

    @Serializable
    data class PlayListDetail(val playListId: String, val label: String) : CustomTab {
        override fun MediaContentRepository.contentFlow() =
            getAudiosOfPlayListFlow(playListId.toLong())
    }
}

@Serializable
data class CurrentCustomTabs(
    val customTabs: List<CustomTab>
)