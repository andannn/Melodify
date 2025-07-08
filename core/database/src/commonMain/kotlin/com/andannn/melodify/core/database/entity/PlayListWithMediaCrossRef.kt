/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.andannn.melodify.core.database.Tables

internal object PlayListWithMediaCrossRefColumns {
    const val ID = "play_list_with_media_cross_ref_id"
    const val PLAY_LIST_ID = "play_list_with_media_cross_ref_play_list_id"
    const val MEDIA_STORE_ID = "play_list_with_media_cross_ref_media_store_id"
    const val ADDED_DATE = "play_list_with_media_cross_ref_added_date"
    const val ARTIST = "play_list_with_media_cross_ref_song_artist"
    const val TITLE = "play_list_with_media_cross_ref_song_title"
}

@Entity(
    tableName = Tables.PLAY_LIST_WITH_MEDIA_CROSS_REF,
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
    @ColumnInfo(name = PlayListWithMediaCrossRefColumns.ID)
    val id: Long = 0,
    @ColumnInfo(name = PlayListWithMediaCrossRefColumns.PLAY_LIST_ID)
    val playListId: Long,
    @ColumnInfo(name = PlayListWithMediaCrossRefColumns.MEDIA_STORE_ID)
    val mediaStoreId: String,
    @ColumnInfo(name = PlayListWithMediaCrossRefColumns.ADDED_DATE)
    val addedDate: Long,
    @ColumnInfo(name = PlayListWithMediaCrossRefColumns.ARTIST)
    val artist: String,
    @ColumnInfo(name = PlayListWithMediaCrossRefColumns.TITLE)
    val title: String,
)
