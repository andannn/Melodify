/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.model

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
    data object AllArtist : LibraryDataSource

    @Serializable
    data object AllAlbum : LibraryDataSource

    @Serializable
    data object AllGenre : LibraryDataSource

    @Serializable
    data object AllPlaylist : LibraryDataSource

    @Serializable
    data object Favorite : LibraryDataSource

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
    ) : LibraryDataSource

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
        fun parseFromString(code: String): LibraryDataSource =
            if (code == "AllSong") {
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

fun LibraryDataSource.browseable() =
    when (this) {
        LibraryDataSource.AllAlbum,
        LibraryDataSource.AllArtist,
        LibraryDataSource.AllGenre,
        LibraryDataSource.AllPlaylist,
        -> true
        LibraryDataSource.AllSong,
        is LibraryDataSource.AlbumDetail,
        is LibraryDataSource.ArtistDetail,
        LibraryDataSource.Favorite,
        is LibraryDataSource.GenreDetail,
        is LibraryDataSource.PlayListDetail,
        -> false
    }

fun MediaItemModel.asLibraryDataSource() =
    when (this) {
        is AlbumItemModel -> LibraryDataSource.AlbumDetail(id)
        is ArtistItemModel -> LibraryDataSource.ArtistDetail(id)
        is GenreItemModel -> LibraryDataSource.GenreDetail(id)
        is PlayListItemModel -> LibraryDataSource.PlayListDetail(id)
        is AudioItemModel -> error("AudioItemModel should not be converted to DataSource")
        is VideoItemModel -> error("VideoItemModel should not be converted to DataSource")
    }

fun ShortcutItem.toDataSource() =
    when (this) {
        ShortcutItem.ALL_SONG -> LibraryDataSource.AllSong
        ShortcutItem.ALBUM -> LibraryDataSource.AllAlbum
        ShortcutItem.ARTIST -> LibraryDataSource.AllArtist
        ShortcutItem.GENRE -> LibraryDataSource.AllGenre
        ShortcutItem.FAVORITE -> LibraryDataSource.Favorite
        ShortcutItem.PLAYLIST -> LibraryDataSource.AllPlaylist
    }
