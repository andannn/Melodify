package com.andannn.melodify.core.data

import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.GenreItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.PlayListItemModel

suspend fun Repository.getAudios(source: MediaItemModel) =
    when (source) {
        is AlbumItemModel -> {
            mediaContentRepository.getAudiosOfAlbum(source.id)
        }

        is ArtistItemModel -> {
            mediaContentRepository.getAudiosOfArtist(source.id)
        }

        is GenreItemModel -> {
            mediaContentRepository.getAudiosOfGenre(source.id)
        }

        is AudioItemModel -> {
            listOf(source)
        }

        is PlayListItemModel -> {
            playListRepository.getAudiosOfPlayList(source.id.toLong())
        }
    }
