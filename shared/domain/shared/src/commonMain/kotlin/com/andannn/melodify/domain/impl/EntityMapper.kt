/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.domain.impl

import com.andannn.melodify.core.database.CustomTabType
import com.andannn.melodify.core.database.SortOptionData
import com.andannn.melodify.core.database.entity.AlbumEntity
import com.andannn.melodify.core.database.entity.ArtistEntity
import com.andannn.melodify.core.database.entity.AudioEntity
import com.andannn.melodify.core.database.entity.CustomTabSortRuleEntity
import com.andannn.melodify.core.database.entity.GenreEntity
import com.andannn.melodify.core.database.entity.LyricEntity
import com.andannn.melodify.core.database.entity.TabEntity
import com.andannn.melodify.core.database.entity.VideoEntity
import com.andannn.melodify.core.database.model.AudioVideoMergedResult
import com.andannn.melodify.core.database.model.LibraryContentSearchResult
import com.andannn.melodify.core.database.model.PlayListWithMediaCount
import com.andannn.melodify.domain.model.AlbumItemModel
import com.andannn.melodify.domain.model.ArtistItemModel
import com.andannn.melodify.domain.model.AudioItemModel
import com.andannn.melodify.domain.model.GenreItemModel
import com.andannn.melodify.domain.model.LyricModel
import com.andannn.melodify.domain.model.MatchedContentTitle
import com.andannn.melodify.domain.model.MediaType
import com.andannn.melodify.domain.model.PlayListItemModel
import com.andannn.melodify.domain.model.PlayerState
import com.andannn.melodify.domain.model.SortOption
import com.andannn.melodify.domain.model.Tab
import com.andannn.melodify.domain.model.TabSortRule
import com.andannn.melodify.domain.model.VideoItemModel
import kotlin.Long

fun List<AlbumEntity>.mapToAlbumItemModel() =
    map {
        it.toAppItem()
    }

fun List<AudioEntity>.mapToAudioItemModel() =
    map {
        it.toAppItem()
    }

fun List<AudioVideoMergedResult>.mapToMediaItemModel() =
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

fun AudioEntity.toAppItem() =
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
        trackCount = mediaCount,
    )

fun AudioVideoMergedResult.toAppItem() =
    toEntity().let {
        if (it is AudioEntity) {
            it.toAppItem()
        } else if (it is VideoEntity) {
            it.toAppItem()
        } else {
            error("no supported")
        }
    }

private fun AudioVideoMergedResult.toEntity() =
    if (audio_id != null) {
        AudioEntity(
            id = audio_id!!,
            path = audio_path,
            sourceUri = audio_sourceUri,
            title = audio_title,
            duration = audio_duration,
            modifiedDate = audio_modifiedDate,
            size = audio_size,
            mimeType = audio_mimeType,
            album = audio_album,
            albumId = audio_albumId,
            artist = audio_artist,
            artistId = audio_artistId,
            cdTrackNumber = audio_cdTrackNumber,
            discNumber = audio_discNumber,
            numTracks = audio_numTracks,
            bitrate = audio_bitrate,
            genre = audio_genre,
            genreId = audio_genreId,
            year = audio_year,
            track = audio_track,
            composer = audio_composer,
            cover = audio_cover,
            deleted = audio_deleted,
        )
    } else {
        VideoEntity(
            id = video_id!!,
            path = video_path,
            sourceUri = video_sourceUri,
            title = video_title,
            duration = video_duration,
            modifiedDate = video_modifiedDate,
            size = video_size,
            mimeType = video_mimeType,
            width = video_width,
            height = video_height,
            orientation = video_orientation,
            resolution = video_resolution,
            relativePath = video_relativePath,
            bucketId = video_bucketId,
            bucketDisplayName = video_bucketDisplayName,
            volumeName = video_volumeName,
            album = video_album,
            artist = video_artist,
            dateAdded = video_dateAdded,
            dateModified = video_dateModified,
            deleted = video_deleted,
        )
    }

fun List<TabEntity>.mapToCustomTabModel() =
    map {
        it.toAppItem()
    }

fun TabEntity.toAppItem() =
    when (type) {
        CustomTabType.ALL_MUSIC -> {
            Tab.AllMusic(tabId = id)
        }

        CustomTabType.ALL_VIDEO -> {
            Tab.AllVideo(tabId = id)
        }

        CustomTabType.ALBUM_DETAIL -> {
            Tab.AlbumDetail(tabId = id, externalId!!, name!!)
        }

        CustomTabType.ARTIST_DETAIL -> {
            Tab.ArtistDetail(tabId = id, externalId!!, name!!)
        }

        CustomTabType.GENRE_DETAIL -> {
            Tab.GenreDetail(tabId = id, externalId!!, name!!)
        }

        CustomTabType.PLAYLIST_DETAIL -> {
            Tab.PlayListDetail(
                tabId = id,
                externalId!!,
                name!!,
            )
        }

        CustomTabType.VIDEO_PLAYLIST_DETAIL -> {
            Tab.PlayListDetail(
                tabId = id,
                externalId!!,
                name!!,
            )
        }

        CustomTabType.VIDEO_BUCKET -> {
            Tab.BucketDetail(tabId = id, externalId!!, name!!)
        }

        else -> {
            null
        }
    }

fun CustomTabSortRuleEntity.toModel() =
    TabSortRule(
        primaryGroupSort = primaryGroupSort.toModel(),
        secondaryGroupSort = secondaryGroupSort.toModel(),
        contentSort = contentSort.toModel(),
        isPreset = isPreset,
    )

fun TabSortRule.toEntity(bindTabId: Long): CustomTabSortRuleEntity =
    CustomTabSortRuleEntity(
        foreignKey = bindTabId,
        primaryGroupSort = primaryGroupSort.toEntity(),
        secondaryGroupSort = secondaryGroupSort.toEntity(),
        contentSort = contentSort.toEntity(),
        isPreset = isPreset,
    )

fun SortOptionData?.toModel() =
    if (this == null) {
        SortOption.NONE
    } else {
        when (type) {
            SortOptionData.SORT_TYPE_AUDIO_ALBUM -> {
                SortOption.AudioOption.Album(isAscending)
            }

            SortOptionData.SORT_TYPE_AUDIO_ARTIST -> {
                SortOption.AudioOption.Artist(isAscending)
            }

            SortOptionData.SORT_TYPE_AUDIO_GENRE -> {
                SortOption.AudioOption.Genre(isAscending)
            }

            SortOptionData.SORT_TYPE_AUDIO_TITLE -> {
                SortOption.AudioOption.Title(isAscending)
            }

            SortOptionData.SORT_TYPE_AUDIO_YEAR -> {
                SortOption.AudioOption.ReleaseYear(isAscending)
            }

            SortOptionData.SORT_TYPE_AUDIO_TRACK_NUM -> {
                SortOption.AudioOption.TrackNum(isAscending)
            }

            SortOptionData.SORT_TYPE_VIDEO_BUCKET_NAME -> {
                SortOption.VideoOption.Bucket(isAscending)
            }

            SortOptionData.SORT_TYPE_VIDEO_TITLE_NAME -> {
                SortOption.VideoOption.Title(isAscending)
            }

            SortOptionData.SORT_TYPE_PLAYLIST_CREATE_DATE -> {
                SortOption.PlayListOption.CreateData(
                    isAscending,
                )
            }

            else -> {
                SortOption.NONE
            }
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

        is SortOption.PlayListOption.CreateData -> {
            SortOptionData(
                type = SortOptionData.SORT_TYPE_PLAYLIST_CREATE_DATE,
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
