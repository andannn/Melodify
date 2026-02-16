/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.domain.model

enum class MediaType {
    AUDIO,
    VIDEO,
    ALBUM,
    ARTIST,
    GENRE,
    PLAYLIST,
}

sealed interface MediaItemModel {
    val id: Long
    val name: String
    val artWorkUri: String?
    val trackCount: Int
}

data class AudioItemModel constructor(
    override val id: Long,
    override val name: String,
    override val artWorkUri: String?,
    val path: String,
    val modifiedDate: Long,
    val album: String,
    val albumId: Long,
    val genre: String,
    val genreId: Long,
    val artist: String,
    val artistId: Long,
    val releaseYear: String,
    val cdTrackNumber: Int,
    val discNumber: Int,
    val source: String,
    val extraUniqueId: String? = null,
    override val trackCount: Int = -1,
) : MediaItemModel

data class VideoItemModel constructor(
    override val id: Long,
    override val name: String,
    override val artWorkUri: String?,
    val bucketId: Long,
    val bucketName: String,
    val path: String,
    val modifiedDate: Long,
    val duration: Int,
    val size: Int,
    val mimeType: String,
    val width: Int,
    val height: Int,
    val resolution: String,
    val relativePath: String,
    val source: String,
    val extraUniqueId: String? = null,
    override val trackCount: Int = -1,
) : MediaItemModel

data class AlbumItemModel(
    override val id: Long,
    override val name: String,
    override val artWorkUri: String?,
    override val trackCount: Int,
) : MediaItemModel

data class ArtistItemModel constructor(
    override val id: Long,
    override val name: String,
    override val artWorkUri: String?,
    override val trackCount: Int,
) : MediaItemModel

data class GenreItemModel(
    override val id: Long,
    override val name: String,
    override val artWorkUri: String?,
    override val trackCount: Int,
) : MediaItemModel

data class PlayListItemModel constructor(
    override val id: Long,
    override val name: String,
    override val artWorkUri: String?,
    override val trackCount: Int,
    val isFavoritePlayList: Boolean,
) : MediaItemModel

val MediaItemModel.browsable
    get() =
        when (this) {
            is AlbumItemModel,
            is ArtistItemModel,
            is GenreItemModel,
            is PlayListItemModel,
            -> true

            is AudioItemModel,
            is VideoItemModel,
            -> false
        }

val MediaItemModel.extraUniqueId
    get() =
        when (this) {
            is AudioItemModel -> extraUniqueId
            is VideoItemModel -> extraUniqueId
            else -> error("Not supported")
        }

val MediaItemModel.subTitle
    get() =
        when (this) {
            is AudioItemModel -> artist
            is VideoItemModel -> bucketName
            else -> error("Not supported")
        }
