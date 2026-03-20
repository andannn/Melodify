/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "library_genre_table")
class GenreEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "genre_id")
    val genreId: Long? = null,
    @ColumnInfo(name = "genre_name")
    val name: String? = null,
)
