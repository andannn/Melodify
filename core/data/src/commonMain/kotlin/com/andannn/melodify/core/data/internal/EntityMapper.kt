/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.internal

import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.core.data.model.DisplaySetting
import com.andannn.melodify.core.data.model.GenreItemModel
import com.andannn.melodify.core.data.model.LyricModel
import com.andannn.melodify.core.data.model.PlayListItemModel
import com.andannn.melodify.core.data.model.SortOption
import com.andannn.melodify.core.database.entity.AlbumEntity
import com.andannn.melodify.core.database.entity.ArtistEntity
import com.andannn.melodify.core.database.entity.CrossRefWithMediaRelation
import com.andannn.melodify.core.database.entity.CustomTabEntity
import com.andannn.melodify.core.database.entity.CustomTabType
import com.andannn.melodify.core.database.entity.GenreEntity
import com.andannn.melodify.core.database.entity.LyricEntity
import com.andannn.melodify.core.database.entity.MediaEntity
import com.andannn.melodify.core.database.entity.PlayListWithMediaCount
import com.andannn.melodify.core.database.entity.SortOptionData
import com.andannn.melodify.core.database.entity.SortRuleEntity
import com.andannn.melodify.core.database.entity.valid
import com.andannn.melodify.core.network.model.LyricData

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
        genreId = genreId?.toString() ?: "",
        genre = genre ?: "",
        artistId = artistId?.toString() ?: "",
        cdTrackNumber = cdTrackNumber ?: 0,
        discNumber = discNumber ?: 0,
        releaseYear = year?.toString() ?: "Unknown",
        source = sourceUri ?: error("No source uri"),
    )

internal fun AlbumEntity.toAppItem() =
    AlbumItemModel(
        id = albumId.toString(),
        name = title,
        artWorkUri = coverUri ?: "",
        trackCount = trackCount,
    )

internal fun ArtistEntity.toAppItem() =
    ArtistItemModel(
        id = artistId.toString(),
        name = name,
        // TODO:
        artWorkUri = null,
        trackCount = trackCount,
    )

internal fun GenreEntity.toAppItem() =
    GenreItemModel(
        id = genreId.toString(),
        name = name ?: "V.A.",
        // TODO:
        artWorkUri = null,
        trackCount = 0,
    )

internal fun PlayListWithMediaCount.toAppItem() =
    PlayListItemModel(
        id = playListEntity.id.toString(),
        name = playListEntity.name,
        artWorkUri = playListEntity.artworkUri ?: "",
        trackCount = mediaCount,
    )

internal fun CrossRefWithMediaRelation.mapToAppItem(): AudioItemModel {
    val entity = this
    val media = entity.media
    return if (media.valid) {
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
            genreId = "",
            genre = "",
            cdTrackNumber = 0,
            discNumber = 0,
            source = "",
            releaseYear = "",
        )
    }
}

internal fun List<CustomTabEntity>.mapToCustomTabModel() =
    map {
        it.toAppItem()
    }

internal fun CustomTabEntity.toAppItem() =
    when (type) {
        CustomTabType.ALL_MUSIC -> CustomTab.AllMusic(tabId = id)
        CustomTabType.ALBUM_DETAIL -> CustomTab.AlbumDetail(tabId = id, externalId!!, name!!)
        CustomTabType.ARTIST_DETAIL -> CustomTab.ArtistDetail(tabId = id, externalId!!, name!!)
        CustomTabType.GENRE_DETAIL -> CustomTab.GenreDetail(tabId = id, externalId!!, name!!)
        CustomTabType.PLAYLIST_DETAIL -> CustomTab.PlayListDetail(tabId = id, externalId!!, name!!)

        else -> null
    }

internal fun CustomTab.toEntity() =
    when (this) {
        is CustomTab.AllMusic ->
            CustomTabEntity(
                id = tabId,
                type = CustomTabType.ALL_MUSIC,
                externalId = "",
            )

        is CustomTab.ArtistDetail ->
            CustomTabEntity(
                id = tabId,
                type = CustomTabType.ARTIST_DETAIL,
                externalId = artistId,
                name = label,
            )

        is CustomTab.GenreDetail ->
            CustomTabEntity(
                id = tabId,
                type = CustomTabType.GENRE_DETAIL,
                externalId = genreId,
                name = label,
            )

        is CustomTab.PlayListDetail ->
            CustomTabEntity(
                id = tabId,
                type = CustomTabType.PLAYLIST_DETAIL,
                externalId = playListId,
                name = label,
            )

        is CustomTab.AlbumDetail ->
            CustomTabEntity(
                id = tabId,
                type = CustomTabType.ALBUM_DETAIL,
                externalId = albumId,
                name = label,
            )
    }

internal fun SortRuleEntity.toModel() =
    DisplaySetting(
        primaryGroupSort = primaryGroupSort.toModel(),
        secondaryGroupSort = secondaryGroupSort.toModel(),
        contentSort = contentSort.toModel(),
        showTrackNum = showTrackNum,
        isPreset = isPreset,
    )

internal fun DisplaySetting.toEntity(bindTabId: Long): SortRuleEntity =
    SortRuleEntity(
        foreignKey = bindTabId,
        primaryGroupSort = primaryGroupSort.toEntity(),
        secondaryGroupSort = secondaryGroupSort.toEntity(),
        contentSort = contentSort.toEntity(),
        showTrackNum = showTrackNum,
        isPreset = isPreset,
    )

internal fun SortOptionData?.toModel() =
    if (this == null) {
        SortOption.NONE
    } else {
        when (type) {
            SortOptionData.SORT_TYPE_ALBUM -> SortOption.Album(isAscending)
            SortOptionData.SORT_TYPE_ARTIST -> SortOption.Artist(isAscending)
            SortOptionData.SORT_TYPE_GENRE -> SortOption.Genre(isAscending)
            SortOptionData.SORT_TYPE_TITLE -> SortOption.Title(isAscending)
            SortOptionData.SORT_TYPE_YEAR -> SortOption.ReleaseYear(isAscending)
            SortOptionData.SORT_TYPE_TRACK_NUM -> SortOption.TrackNum(isAscending)
            else -> SortOption.NONE
        }
    }

internal fun SortOption.toEntity() =
    when (this) {
        is SortOption.Album ->
            SortOptionData(
                type = SortOptionData.SORT_TYPE_ALBUM,
                isAscending = ascending,
            )

        is SortOption.Artist ->
            SortOptionData(
                type = SortOptionData.SORT_TYPE_ARTIST,
                isAscending = ascending,
            )

        is SortOption.Genre ->
            SortOptionData(
                type = SortOptionData.SORT_TYPE_GENRE,
                isAscending = ascending,
            )

        is SortOption.ReleaseYear ->
            SortOptionData(
                type = SortOptionData.SORT_TYPE_YEAR,
                isAscending = ascending,
            )

        is SortOption.Title ->
            SortOptionData(
                type = SortOptionData.SORT_TYPE_TITLE,
                isAscending = ascending,
            )

        is SortOption.TrackNum ->
            SortOptionData(
                type = SortOptionData.SORT_TYPE_TRACK_NUM,
                isAscending = ascending,
            )

        SortOption.NONE -> null
    }

internal fun LyricEntity.toLyricModel(): LyricModel =
    LyricModel(
        plainLyrics = plainLyrics,
        syncedLyrics = syncedLyrics,
    )

internal fun LyricData.toLyricEntity() =
    LyricEntity(
        id = id,
        name = name,
        trackName = trackName,
        artistName = artistName,
        albumName = albumName,
        duration = duration,
        instrumental = instrumental,
        plainLyrics = plainLyrics,
        syncedLyrics = syncedLyrics,
    )
