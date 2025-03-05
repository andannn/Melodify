package com.andannn.melodify.core.data.model

import com.andannn.melodify.core.data.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

@Serializable
sealed interface CustomTab {
    fun Repository.contentFlow(): Flow<List<AudioItemModel>>

    @Serializable
    data object AllMusic : CustomTab {
        override fun Repository.contentFlow() = mediaContentRepository.getAllMediaItemsFlow()
    }

    @Serializable
    data class AlbumDetail(val albumId: String, val label: String) : CustomTab {
        override fun Repository.contentFlow() = mediaContentRepository.getAudiosOfAlbumFlow(albumId)
    }

    @Serializable
    data class ArtistDetail(val artistId: String, val label: String) : CustomTab {
        override fun Repository.contentFlow() = mediaContentRepository.getAudiosOfArtistFlow(artistId)
    }

    @Serializable
    data class GenreDetail(val genreId: String, val label: String) : CustomTab {
        override fun Repository.contentFlow() = mediaContentRepository.getAudiosOfGenreFlow(genreId)
    }

    @Serializable
    data class PlayListDetail(val playListId: String, val label: String) : CustomTab {
        override fun Repository.contentFlow() =
            playListRepository.getAudiosOfPlayListFlow(playListId.toLong())
    }
}
