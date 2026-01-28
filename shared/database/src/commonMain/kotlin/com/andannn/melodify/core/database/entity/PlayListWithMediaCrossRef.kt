/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

internal object PlayListWithMediaCrossRefColumns {
    const val ID = "play_list_with_media_cross_ref_id"
    const val PLAY_LIST_ID = "play_list_with_media_cross_ref_play_list_id"
    const val MEDIA_STORE_ID = "play_list_with_media_cross_ref_media_store_id"
    const val TITLE = "play_list_with_media_cross_ref_song_title"
}

@Entity(
    tableName = "play_list_with_media_cross_ref_table",
    indices = [
        Index(
            value = [
                PlayListWithMediaCrossRefColumns.PLAY_LIST_ID,
                PlayListWithMediaCrossRefColumns.MEDIA_STORE_ID,
            ],
            unique = true,
        ),
    ],
)
data class PlayListWithMediaCrossRef(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "play_list_with_media_cross_ref_id")
    val id: Long = 0,
    @ColumnInfo(name = "play_list_with_media_cross_ref_play_list_id")
    val playListId: Long,
    @ColumnInfo(name = "play_list_with_media_cross_ref_media_store_id")
    val mediaStoreId: String,
    @ColumnInfo(name = "play_list_with_media_cross_ref_added_date")
    val addedDate: Long,
    @ColumnInfo(name = "play_list_with_media_cross_ref_song_artist")
    val artist: String,
    @ColumnInfo(name = "play_list_with_media_cross_ref_song_title")
    val title: String,
)
