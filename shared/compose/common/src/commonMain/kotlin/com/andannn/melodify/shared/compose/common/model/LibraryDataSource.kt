/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.common.model

import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.GenreItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.PlayListItemModel
import com.andannn.melodify.core.data.model.VideoItemModel
import kotlinx.serialization.Serializable

@Serializable
sealed interface LibraryDataSource {
    @Serializable
    data object AllSong : LibraryDataSource

    @Serializable
    data object AllVideo : LibraryDataSource

    @Serializable
    data object AllArtist : LibraryDataSource

    @Serializable
    data object AllAlbum : LibraryDataSource

    @Serializable
    data object AllGenre : LibraryDataSource

    @Serializable
    data object AllPlaylist : LibraryDataSource

    @Serializable
    data class ArtistDetail(
        val id: String,
    ) : LibraryDataSource

    @Serializable
    data class AlbumDetail(
        val id: String,
    ) : LibraryDataSource

    @Serializable
    data class GenreDetail(
        val id: String,
    ) : LibraryDataSource

    @Serializable
    data class PlayListDetail(
        val id: String,
        val isAudioPlayList: Boolean,
    ) : LibraryDataSource
}

fun LibraryDataSource.browseable() =
    when (this) {
        LibraryDataSource.AllAlbum,
        LibraryDataSource.AllArtist,
        LibraryDataSource.AllGenre,
        LibraryDataSource.AllPlaylist,
        -> true

        LibraryDataSource.AllVideo,
        LibraryDataSource.AllSong,
        is LibraryDataSource.AlbumDetail,
        is LibraryDataSource.ArtistDetail,
        is LibraryDataSource.GenreDetail,
        is LibraryDataSource.PlayListDetail,
        -> false
    }

fun MediaItemModel.asLibraryDataSource() =
    when (this) {
        is AlbumItemModel -> LibraryDataSource.AlbumDetail(id)
        is ArtistItemModel -> LibraryDataSource.ArtistDetail(id)
        is GenreItemModel -> LibraryDataSource.GenreDetail(id)
        is PlayListItemModel -> LibraryDataSource.PlayListDetail(id, isAudioPlayList = isAudioPlayList)
        is AudioItemModel -> error("AudioItemModel should not be converted to DataSource")
        is VideoItemModel -> error("VideoItemModel should not be converted to DataSource")
    }

fun ShortcutItem.toDataSource() =
    when (this) {
        ShortcutItem.ALL_SONG -> LibraryDataSource.AllSong
        ShortcutItem.ALBUM -> LibraryDataSource.AllAlbum
        ShortcutItem.ARTIST -> LibraryDataSource.AllArtist
        ShortcutItem.GENRE -> LibraryDataSource.AllGenre
        ShortcutItem.PLAYLIST -> LibraryDataSource.AllPlaylist
        ShortcutItem.ALL_VIDEO -> LibraryDataSource.AllVideo
    }
