/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andannn.melodify.core.database.Tables

internal object ArtistColumns {
    const val ID = "artist_id"
    const val COVER_URI = "artist_cover_uri"
    const val NAME = "artist_name"
    const val TRACK_COUNT = "artist_track_count"
}

@Entity(tableName = Tables.LIBRARY_ARTIST)
data class ArtistEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = ArtistColumns.ID)
    val artistId: Long,
    @ColumnInfo(name = ArtistColumns.NAME)
    val name: String,
    @ColumnInfo(name = ArtistColumns.COVER_URI)
    val artistCoverUri: String? = null,
    @ColumnInfo(name = ArtistColumns.TRACK_COUNT, defaultValue = "0")
    val trackCount: Int = 0,
)

fun ArtistEntity.toArtistWithoutTrackCount() = ArtistWithoutTrackCount(artistId, name, artistCoverUri)

data class ArtistWithoutTrackCount(
    @ColumnInfo(name = ArtistColumns.ID)
    val artistId: Long,
    @ColumnInfo(name = ArtistColumns.NAME)
    val name: String,
    @ColumnInfo(name = ArtistColumns.COVER_URI)
    val artistCoverUri: String? = null,
)
