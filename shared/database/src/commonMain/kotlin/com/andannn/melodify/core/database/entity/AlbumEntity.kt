/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "library_album_table")
data class AlbumEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "album_id")
    val albumId: Long,
    @ColumnInfo(name = "album_title")
    val title: String,
    @ColumnInfo(name = "album_cover_uri")
    val coverUri: String? = null,
)
