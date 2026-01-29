/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "library_artist_table")
data class ArtistEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "artist_id")
    val artistId: Long,
    @ColumnInfo(name = "artist_name")
    val name: String,
    @ColumnInfo(name = "artist_cover_uri")
    val artistCoverUri: String? = null,
    @ColumnInfo(name = "artist_track_count", defaultValue = "0")
    val trackCount: Int = 0,
)

internal fun ArtistEntity.toArtistWithoutTrackCount() = ArtistWithoutTrackCount(artistId, name, artistCoverUri)

data class ArtistWithoutTrackCount(
    @ColumnInfo(name = "artist_id")
    val artistId: Long,
    @ColumnInfo(name = "artist_name")
    val name: String,
    @ColumnInfo(name = "artist_cover_uri")
    val artistCoverUri: String? = null,
)
