/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.entity

import androidx.room3.ColumnInfo
import androidx.room3.Embedded
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "library_artist_table")
data class ArtistEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "artist_id")
    val artistId: Long,
    @ColumnInfo(name = "artist_name")
    val name: String,
    @ColumnInfo(name = "artist_cover_uri")
    val artistCoverUri: String? = null,
)
