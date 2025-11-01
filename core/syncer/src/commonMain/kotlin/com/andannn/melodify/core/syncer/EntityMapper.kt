/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.syncer

import com.andannn.melodify.core.database.entity.AlbumEntity
import com.andannn.melodify.core.database.entity.ArtistEntity
import com.andannn.melodify.core.database.entity.GenreEntity
import com.andannn.melodify.core.database.entity.MediaEntity
import com.andannn.melodify.core.syncer.model.AlbumData
import com.andannn.melodify.core.syncer.model.ArtistData
import com.andannn.melodify.core.syncer.model.AudioData
import com.andannn.melodify.core.syncer.model.GenreData

fun List<AlbumData>.toAlbumEntity(): List<AlbumEntity> =
    map {
        AlbumEntity(
            albumId = it.albumId,
            title = it.title,
            numberOfSongsForArtist = it.numberOfSongsForArtist,
            coverUri = it.coverUri,
        )
    }

fun List<GenreData>.toGenreEntity(): List<GenreEntity> =
    map {
        GenreEntity(
            genreId = it.genreId,
            name = it.name,
        )
    }

fun List<ArtistData>.toArtistEntity(): List<ArtistEntity> =
    map {
        ArtistEntity(
            artistId = it.artistId,
            name = it.name,
            artistCoverUri = it.artistCoverUri,
        )
    }

fun List<AudioData>.toMediaEntity(): List<MediaEntity> =
    map {
        MediaEntity(
            id = it.id,
            path = it.path,
            sourceUri = it.sourceUri,
            title = it.title,
            albumId = it.albumId,
            artistId = it.artistId,
            genreId = it.genreId,
            duration = it.duration,
            year = it.year,
            size = it.size,
            mimeType = it.mimeType,
            album = it.album,
            artist = it.artist,
            genre = it.genre,
            composer = it.composer,
            cdTrackNumber = it.cdTrackNumber,
            discNumber = it.discNumber,
            numTracks = it.numTracks,
            bitrate = it.bitrate,
            modifiedDate = it.modifiedDate,
            cover = it.cover,
        )
    }
