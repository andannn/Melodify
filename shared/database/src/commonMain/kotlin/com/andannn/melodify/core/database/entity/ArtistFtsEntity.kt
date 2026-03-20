/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Fts4
import androidx.room3.PrimaryKey

@Fts4(
    contentEntity = ArtistEntity::class,
)
@Entity(tableName = "library_fts_artist_table")
data class ArtistFtsEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    val rowId: Int,
    @ColumnInfo(name = "artist_name")
    val title: String,
)
