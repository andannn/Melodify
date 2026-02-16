/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_tab_table")
data class TabEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "custom_tab_id")
    val id: Long = 0,
    /**
     *  values of [TabType]
     */
    @ColumnInfo(name = "custom_tab_type")
    val type: String,
    @ColumnInfo(name = "custom_tab_name")
    val name: String? = null,
    @ColumnInfo(name = "custom_tab_external_id")
    val externalId: String? = null,
    @ColumnInfo(name = "sort_order")
    val sortOrder: Int? = 0,
)

object TabType {
    const val ALL_MUSIC = "all_music"
    const val ALL_VIDEO = "all_video"
    const val ALBUM_DETAIL = "album_detail"
    const val ARTIST_DETAIL = "artist_detail"
    const val GENRE_DETAIL = "genre_detail"
    const val PLAYLIST_DETAIL = "playlist_detail"
    const val VIDEO_BUCKET = "video_bucket"
}
