/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andannn.melodify.core.database.Tables

internal object PlayListColumns {
    const val ID = "play_list_id"
    const val NAME = "play_list_name"
    const val CREATED_DATE = "play_list_created_date"
    const val ARTWORK_URI = "play_list_artwork_uri"
    const val IS_AUDIO_PLAYLIST = "is_audio_playlist"
}

@Entity(tableName = Tables.PLAY_LIST)
data class PlayListEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = PlayListColumns.ID)
    val id: Long = 0,
    @ColumnInfo(name = PlayListColumns.CREATED_DATE)
    val createdDate: Long,
    @ColumnInfo(name = PlayListColumns.NAME)
    val name: String,
    @ColumnInfo(name = PlayListColumns.ARTWORK_URI)
    val artworkUri: String?,
    @ColumnInfo(name = PlayListColumns.IS_AUDIO_PLAYLIST)
    val isAudioPlayList: Boolean? = true,
)
