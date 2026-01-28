/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

object MediaColumns {
    const val ID = "media_id"
    const val TITLE = "media_title"
    const val SOURCE_URI = "source_uri"
    const val ALBUM = "media_album"
    const val ALBUM_ID = "media_album_id"
    const val ARTIST = "media_artist"
    const val ARTIST_ID = "media_artist_id"
    const val CD_TRACK_NUMBER = "media_cd_track_number"
    const val GENRE = "media_genre"
    const val GENRE_ID = "media_genre_id"
    const val YEAR = "media_year"
    const val DELETED = "deleted"
}

@Entity(
    tableName = "library_media_table",
)
data class MediaEntity constructor(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "media_id")
    val id: Long = 0,
    @ColumnInfo(name = "file_path")
    val path: String? = null,
    @ColumnInfo(name = "source_uri")
    val sourceUri: String? = null,
    @ColumnInfo(name = "media_title")
    val title: String? = null,
    @ColumnInfo(name = "media_duration")
    val duration: Int? = null,
    @ColumnInfo(name = "media_modified_date")
    val modifiedDate: Long? = null,
    @ColumnInfo(name = "media_size")
    val size: Int? = null,
    @ColumnInfo(name = "media_mime_type")
    val mimeType: String? = null,
    @ColumnInfo(name = "media_album")
    val album: String? = null,
    @ColumnInfo(name = "media_album_id")
    val albumId: Long? = null,
    @ColumnInfo(name = "media_artist")
    val artist: String? = null,
    @ColumnInfo(name = "media_artist_id")
    val artistId: Long? = null,
    @ColumnInfo(name = "media_cd_track_number")
    val cdTrackNumber: Int? = null,
    @ColumnInfo(name = "media_disc_number")
    val discNumber: Int? = null,
    @ColumnInfo(name = "media_num_tracks")
    val numTracks: Int? = null,
    @ColumnInfo(name = "media_bitrate")
    val bitrate: Int? = null,
    @ColumnInfo(name = "media_genre")
    val genre: String? = null,
    @ColumnInfo(name = "media_genre_id")
    val genreId: Long? = null,
    @ColumnInfo(name = "media_year")
    val year: String? = null,
    @ColumnInfo(name = "media_track")
    val track: String? = null,
    @ColumnInfo(name = "media_composer")
    val composer: String? = null,
    @ColumnInfo(name = "media_cover")
    val cover: String? = null,
    @ColumnInfo(name = "deleted")
    val deleted: Int? = null,
)

val MediaEntity.valid: Boolean get() = id != 0L
val VideoEntity.valid: Boolean get() = id != 0L
