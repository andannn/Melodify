/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.entity.fts

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey
import com.andannn.melodify.core.database.entity.AlbumEntity

@Fts4(
    contentEntity = AlbumEntity::class,
)
@Entity(tableName = "library_fts_album_table")
data class AlbumFtsEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    val rowId: Int,
    @ColumnInfo(name = "album_title")
    val title: String,
)
