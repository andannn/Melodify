/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "play_list_table",
)
data class PlayListEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "play_list_id")
    val id: Long = 0,
    @ColumnInfo(name = "play_list_created_date")
    val createdDate: Long,
    @ColumnInfo(name = "play_list_name")
    val name: String,
    @ColumnInfo(name = "play_list_artwork_uri")
    val artworkUri: String?,
    @ColumnInfo(name = "is_favorite_playlist")
    val isFavoritePlayList: Boolean? = false,
)
