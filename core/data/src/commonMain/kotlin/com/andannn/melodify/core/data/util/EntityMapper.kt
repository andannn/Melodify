/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.util

import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.core.data.model.GenreItemModel
import com.andannn.melodify.core.data.model.PlayListItemModel
import com.andannn.melodify.core.database.entity.AlbumEntity
import com.andannn.melodify.core.database.entity.ArtistEntity
import com.andannn.melodify.core.database.entity.CrossRefWithMediaRelation
import com.andannn.melodify.core.database.entity.CustomTabEntity
import com.andannn.melodify.core.database.entity.CustomTabType
import com.andannn.melodify.core.database.entity.GenreEntity
import com.andannn.melodify.core.database.entity.MediaEntity
import com.andannn.melodify.core.database.entity.PlayListWithMediaCount
import com.andannn.melodify.core.database.entity.valid

internal fun List<AlbumEntity>.mapToAlbumItemModel() =
    map {
        it.toAppItem()
    }

internal fun List<MediaEntity>.mapToAudioItemModel() =
    map {
        it.toAppItem()
    }

internal fun List<ArtistEntity>.mapToArtistItemModel() =
    map {
        it.toAppItem()
    }

internal fun List<GenreEntity>.mapToGenreItemModel() =
    map {
        it.toAppItem()
    }

internal fun MediaEntity.toAppItem() =
    AudioItemModel(
        id = id.toString(),
// Desktop app do not support the same item in play queue.
        extraUniqueId = id.toString(),
        name = title ?: "",
        artWorkUri = cover ?: "",
        modifiedDate = modifiedDate ?: -1,
        album = album ?: "",
        albumId = albumId?.toString() ?: "",
        artist = artist ?: "",
        artistId = artistId?.toString() ?: "",
        cdTrackNumber = cdTrackNumber ?: 0,
        discNumber = discNumber ?: 0,
        source = sourceUri ?: error("No source uri"),
    )

internal fun AlbumEntity.toAppItem() =
    AlbumItemModel(
        id = albumId.toString(),
        name = title,
        artWorkUri = coverUri ?: "",
        trackCount = trackCount ?: 0,
    )

internal fun ArtistEntity.toAppItem() =
    ArtistItemModel(
        id = artistId.toString(),
        name = name,
        // TODO:
        artWorkUri = "",
        trackCount = trackCount ?: 0,
    )

internal fun GenreEntity.toAppItem() =
    GenreItemModel(
        id = genreId.toString(),
        name = name ?: "",
        // TODO:
        artWorkUri = "",
        trackCount = 0,
    )

internal fun PlayListWithMediaCount.toAppItem() =
    PlayListItemModel(
        id = playListEntity.id.toString(),
        name = playListEntity.name,
        artWorkUri = playListEntity.artworkUri ?: "",
        trackCount = mediaCount,
    )

internal fun List<CrossRefWithMediaRelation>.mapToAppItemList() =
    map { entity ->
        val media = entity.media
        if (media.valid) {
            media.toAppItem()
        } else {
            AudioItemModel(
                id = AudioItemModel.INVALID_ID_PREFIX + entity.playListWithMediaCrossRef.mediaStoreId,
                name = entity.playListWithMediaCrossRef.title,
                artist = entity.playListWithMediaCrossRef.artist,
                modifiedDate = 0,
                artWorkUri = "",
                album = "",
                albumId = "",
                artistId = "",
                cdTrackNumber = 0,
                discNumber = 0,
                source = "",
            )
        }
    }

internal fun List<CustomTabEntity>.mapToCustomTabModel() =
    map {
        it.toAppItem()
    }

internal fun CustomTabEntity.toAppItem() =
    when (type) {
        CustomTabType.ALL_MUSIC -> CustomTab.AllMusic
        CustomTabType.ALBUM_DETAIL -> CustomTab.AlbumDetail(externalId!!, name!!)
        CustomTabType.ARTIST_DETAIL -> CustomTab.ArtistDetail(externalId!!, name!!)
        CustomTabType.GENRE_DETAIL -> CustomTab.GenreDetail(externalId!!, name!!)
        CustomTabType.PLAYLIST_DETAIL -> CustomTab.PlayListDetail(externalId!!, name!!)

        else -> null
    }

internal fun CustomTab.toEntity() =
    when (this) {
        CustomTab.AllMusic -> CustomTabEntity(type = CustomTabType.ALL_MUSIC, externalId = "")
        is CustomTab.ArtistDetail ->
            CustomTabEntity(
                type = CustomTabType.ARTIST_DETAIL,
                externalId = artistId,
                name = label,
            )

        is CustomTab.GenreDetail ->
            CustomTabEntity(
                type = CustomTabType.GENRE_DETAIL,
                externalId = genreId,
                name = label,
            )
        is CustomTab.PlayListDetail ->
            CustomTabEntity(
                type = CustomTabType.PLAYLIST_DETAIL,
                externalId = playListId,
                name = label,
            )
        is CustomTab.AlbumDetail ->
            CustomTabEntity(
                type = CustomTabType.ALBUM_DETAIL,
                externalId = albumId,
                name = label,
            )
    }
