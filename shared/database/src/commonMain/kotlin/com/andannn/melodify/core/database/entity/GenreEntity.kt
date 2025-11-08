/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andannn.melodify.core.database.Tables

internal object GenreColumns {
    const val ID = "genre_id"
    const val NAME = "genre_name"
}

@Entity(tableName = Tables.LIBRARY_GENRE)
class GenreEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = GenreColumns.ID)
    val genreId: Long? = null,
    @ColumnInfo(name = GenreColumns.NAME)
    val name: String? = null,
)
