package com.andannn.melodify.core.syncer

import com.andannn.melodify.core.database.entity.AlbumEntity
import com.andannn.melodify.core.database.entity.ArtistEntity
import com.andannn.melodify.core.database.entity.GenreEntity
import com.andannn.melodify.core.database.entity.MediaEntity
import com.andannn.melodify.core.library.mediastore.model.AlbumData
import com.andannn.melodify.core.library.mediastore.model.ArtistData
import com.andannn.melodify.core.library.mediastore.model.AudioData
import com.andannn.melodify.core.library.mediastore.model.GenreData

fun List<AlbumData>.toEntity(): List<AlbumEntity> = map {
    AlbumEntity(
        albumId = it.albumId,
        title = it.title,
        trackCount = it.trackCount,
        numberOfSongsForArtist = it.numberOfSongsForArtist,
    )
}

fun List<GenreData>.toEntity(): List<GenreEntity> = map {
    GenreEntity(
        genreId = it.genreId,
        name = it.name,
    )
}

fun List<ArtistData>.toEntity(): List<ArtistEntity> = map {
    ArtistEntity(
        artistId = it.artistId,
        name = it.name,
        artistCoverUri = it.artistCoverUri,
        trackCount = it.trackCount,
    )
}

fun List<AudioData>.toEntity(): List<MediaEntity> = map {
    MediaEntity(
        id = it.id,
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
        track = it.track,
        composer = it.composer,
        cdTrackNumber = it.cdTrackNumber,
        discNumber = it.discNumber,
        numTracks = it.numTracks,
        bitrate = it.bitrate,
        modifiedDate = it.modifiedDate,
    )
}

