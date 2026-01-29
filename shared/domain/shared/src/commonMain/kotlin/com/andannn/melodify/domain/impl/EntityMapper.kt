/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.domain.impl

import com.andannn.melodify.core.database.CustomTabType
import com.andannn.melodify.core.database.SortOptionData
import com.andannn.melodify.core.database.entity.AlbumEntity
import com.andannn.melodify.core.database.entity.ArtistEntity
import com.andannn.melodify.core.database.entity.CustomTabEntity
import com.andannn.melodify.core.database.entity.GenreEntity
import com.andannn.melodify.core.database.entity.LyricEntity
import com.andannn.melodify.core.database.entity.MediaEntity
import com.andannn.melodify.core.database.entity.SortRuleEntity
import com.andannn.melodify.core.database.entity.VideoEntity
import com.andannn.melodify.core.database.entity.valid
import com.andannn.melodify.core.database.model.CrossRefWithMediaRelation
import com.andannn.melodify.core.database.model.CrossRefWithVideoRelation
import com.andannn.melodify.core.database.model.LibraryContentSearchResult
import com.andannn.melodify.core.database.model.PlayListWithMediaCount
import com.andannn.melodify.domain.model.AlbumItemModel
import com.andannn.melodify.domain.model.ArtistItemModel
import com.andannn.melodify.domain.model.AudioItemModel
import com.andannn.melodify.domain.model.CustomTab
import com.andannn.melodify.domain.model.DisplaySetting
import com.andannn.melodify.domain.model.GenreItemModel
import com.andannn.melodify.domain.model.LyricModel
import com.andannn.melodify.domain.model.MatchedContentTitle
import com.andannn.melodify.domain.model.MediaType
import com.andannn.melodify.domain.model.PlayListItemModel
import com.andannn.melodify.domain.model.PlayerState
import com.andannn.melodify.domain.model.SortOption
import com.andannn.melodify.domain.model.VideoItemModel

fun List<AlbumEntity>.mapToAlbumItemModel() =
    map {
        it.toAppItem()
    }

fun List<MediaEntity>.mapToAudioItemModel() =
    map {
        it.toAppItem()
    }

fun List<VideoEntity>.mapToVideoItemModel() =
    map {
        it.toAppItem()
    }

fun List<ArtistEntity>.mapToArtistItemModel() =
    map {
        it.toAppItem()
    }

fun List<GenreEntity>.mapToGenreItemModel() =
    map {
        it.toAppItem()
    }

fun MediaEntity.toAppItem() =
    AudioItemModel(
        id = id.toString(),
        path = path ?: "",
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

fun VideoEntity.toAppItem(): VideoItemModel =
    VideoItemModel(
        id = id.toString(),
        name = title.orEmpty(),
        artWorkUri = sourceUri,
        bucketId = bucketId?.toString().orEmpty(),
        bucketName = bucketDisplayName.orEmpty(),
        path = path.orEmpty(),
        modifiedDate = modifiedDate ?: 0L,
        duration = duration ?: 0,
        size = size ?: 0,
        mimeType = mimeType.orEmpty(),
        width = width ?: 0,
        height = height ?: 0,
        resolution = resolution ?: "${width ?: 0}x${height ?: 0}",
        relativePath = relativePath.orEmpty(),
        source = sourceUri.orEmpty(),
        extraUniqueId = null,
        trackCount = -1,
    )

fun AlbumEntity.toAppItem() =
    AlbumItemModel(
        id = albumId.toString(),
        name = title,
        artWorkUri = coverUri ?: "",
        trackCount = trackCount,
    )

fun ArtistEntity.toAppItem() =
    ArtistItemModel(
        id = artistId.toString(),
        name = name,
        artWorkUri = null,
        trackCount = trackCount,
    )

fun GenreEntity.toAppItem() =
    GenreItemModel(
        id = genreId.toString(),
        name = name ?: "V.A.",
        artWorkUri = null,
        trackCount = 0,
    )

fun PlayListWithMediaCount.toAppItem() =
    PlayListItemModel(
        id = playListEntity.id.toString(),
        name = playListEntity.name,
        artWorkUri = playListEntity.artworkUri ?: "",
        isFavoritePlayList = playListEntity.isFavoritePlayList == true,
        isAudioPlayList = playListEntity.isAudioPlayList == true,
        trackCount = mediaCount,
    )

fun CrossRefWithMediaRelation.mapToAppItem(): AudioItemModel {
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
            path = "",
        )
    }
}

fun CrossRefWithVideoRelation.mapToAppItem(): VideoItemModel {
    val entity = this
    val media = entity.media
    return if (media.valid) {
        media.toAppItem()
    } else {
        VideoItemModel(
            id = AudioItemModel.INVALID_ID_PREFIX + entity.playListWithMediaCrossRef.mediaStoreId,
            name = entity.playListWithMediaCrossRef.title,
            modifiedDate = 0,
            artWorkUri = "",
            bucketId = "",
            bucketName = "",
            path = "",
            duration = 0,
            size = 0,
            mimeType = "",
            width = 0,
            height = 0,
            resolution = "",
            relativePath = "",
            source = "",
        )
    }
}

fun List<CustomTabEntity>.mapToCustomTabModel() =
    map {
        it.toAppItem()
    }

fun CustomTabEntity.toAppItem() =
    when (type) {
        CustomTabType.ALL_MUSIC -> {
            CustomTab.AllMusic(tabId = id)
        }

        CustomTabType.ALL_VIDEO -> {
            CustomTab.AllVideo(tabId = id)
        }

        CustomTabType.ALBUM_DETAIL -> {
            CustomTab.AlbumDetail(tabId = id, externalId!!, name!!)
        }

        CustomTabType.ARTIST_DETAIL -> {
            CustomTab.ArtistDetail(tabId = id, externalId!!, name!!)
        }

        CustomTabType.GENRE_DETAIL -> {
            CustomTab.GenreDetail(tabId = id, externalId!!, name!!)
        }

        CustomTabType.AUDIO_PLAYLIST_DETAIL -> {
            CustomTab.PlayListDetail(
                tabId = id,
                externalId!!,
                name!!,
                isAudio = true,
            )
        }

        CustomTabType.VIDEO_PLAYLIST_DETAIL -> {
            CustomTab.PlayListDetail(
                tabId = id,
                externalId!!,
                name!!,
                isAudio = false,
            )
        }

        CustomTabType.VIDEO_BUCKET -> {
            CustomTab.BucketDetail(tabId = id, externalId!!, name!!)
        }

        else -> {
            null
        }
    }

fun SortRuleEntity.toModel() =
    DisplaySetting(
        primaryGroupSort = primaryGroupSort.toModel(),
        secondaryGroupSort = secondaryGroupSort.toModel(),
        contentSort = contentSort.toModel(),
        showTrackNum = showTrackNum,
        isPreset = isPreset,
    )

fun DisplaySetting.toEntity(bindTabId: Long): SortRuleEntity =
    SortRuleEntity(
        foreignKey = bindTabId,
        primaryGroupSort = primaryGroupSort.toEntity(),
        secondaryGroupSort = secondaryGroupSort.toEntity(),
        contentSort = contentSort.toEntity(),
        showTrackNum = showTrackNum,
        isPreset = isPreset,
    )

fun SortOptionData?.toModel() =
    if (this == null) {
        SortOption.NONE
    } else {
        when (type) {
            SortOptionData.SORT_TYPE_AUDIO_ALBUM -> SortOption.AudioOption.Album(isAscending)
            SortOptionData.SORT_TYPE_AUDIO_ARTIST -> SortOption.AudioOption.Artist(isAscending)
            SortOptionData.SORT_TYPE_AUDIO_GENRE -> SortOption.AudioOption.Genre(isAscending)
            SortOptionData.SORT_TYPE_AUDIO_TITLE -> SortOption.AudioOption.Title(isAscending)
            SortOptionData.SORT_TYPE_AUDIO_YEAR -> SortOption.AudioOption.ReleaseYear(isAscending)
            SortOptionData.SORT_TYPE_AUDIO_TRACK_NUM -> SortOption.AudioOption.TrackNum(isAscending)
            SortOptionData.SORT_TYPE_VIDEO_BUCKET_NAME -> SortOption.VideoOption.Bucket(isAscending)
            SortOptionData.SORT_TYPE_VIDEO_TITLE_NAME -> SortOption.VideoOption.Title(isAscending)
            else -> SortOption.NONE
        }
    }

fun SortOption.toEntity() =
    when (this) {
        is SortOption.AudioOption.Album -> {
            SortOptionData(
                type = SortOptionData.SORT_TYPE_AUDIO_ALBUM,
                isAscending = ascending,
            )
        }

        is SortOption.AudioOption.Artist -> {
            SortOptionData(
                type = SortOptionData.SORT_TYPE_AUDIO_ARTIST,
                isAscending = ascending,
            )
        }

        is SortOption.AudioOption.Genre -> {
            SortOptionData(
                type = SortOptionData.SORT_TYPE_AUDIO_GENRE,
                isAscending = ascending,
            )
        }

        is SortOption.AudioOption.ReleaseYear -> {
            SortOptionData(
                type = SortOptionData.SORT_TYPE_AUDIO_YEAR,
                isAscending = ascending,
            )
        }

        is SortOption.AudioOption.Title -> {
            SortOptionData(
                type = SortOptionData.SORT_TYPE_AUDIO_TITLE,
                isAscending = ascending,
            )
        }

        is SortOption.AudioOption.TrackNum -> {
            SortOptionData(
                type = SortOptionData.SORT_TYPE_AUDIO_TRACK_NUM,
                isAscending = ascending,
            )
        }

        is SortOption.VideoOption.Bucket -> {
            SortOptionData(
                type = SortOptionData.SORT_TYPE_VIDEO_BUCKET_NAME,
                isAscending = ascending,
            )
        }

        is SortOption.VideoOption.Title -> {
            SortOptionData(
                type = SortOptionData.SORT_TYPE_VIDEO_TITLE_NAME,
                isAscending = ascending,
            )
        }

        SortOption.NONE -> {
            null
        }
    }

fun LyricEntity.toLyricModel(): LyricModel =
    LyricModel(
        plainLyrics = plainLyrics,
        syncedLyrics = syncedLyrics,
    )

fun com.andannn.melodify.core.player.PlayerState.toPlayerState(): PlayerState =
    when (this) {
        is com.andannn.melodify.core.player.PlayerState.Error,
        com.andannn.melodify.core.player.PlayerState.Idle,
        is com.andannn.melodify.core.player.PlayerState.PlayBackEnd,
        is com.andannn.melodify.core.player.PlayerState.Paused,
        -> PlayerState.PAUSED

        is com.andannn.melodify.core.player.PlayerState.Playing -> PlayerState.PLAYING

        is com.andannn.melodify.core.player.PlayerState.Buffering -> PlayerState.BUFFERING
    }

fun LibraryContentSearchResult.toModel(): MatchedContentTitle =
    MatchedContentTitle(
        id = id,
        title = title,
        type = contentType.toMediaType(),
    )

fun Int.toMediaType() =
    when (this) {
        com.andannn.melodify.core.database.MediaType.MEDIA -> MediaType.AUDIO
        com.andannn.melodify.core.database.MediaType.ALBUM -> MediaType.ALBUM
        com.andannn.melodify.core.database.MediaType.ARTIST -> MediaType.ARTIST
        com.andannn.melodify.core.database.MediaType.GENRE -> MediaType.GENRE
        com.andannn.melodify.core.database.MediaType.VIDEO -> MediaType.VIDEO
        else -> error("Invalid")
    }
