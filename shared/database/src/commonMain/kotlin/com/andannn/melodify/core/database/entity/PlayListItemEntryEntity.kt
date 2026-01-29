/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "play_list_item_entry_table",
    foreignKeys = [
        ForeignKey(
            entity = PlayListEntity::class,
            parentColumns = ["play_list_id"],
            childColumns = ["play_list_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = MediaEntity::class,
            parentColumns = ["media_id"],
            childColumns = ["audio_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = VideoEntity::class,
            parentColumns = ["video_id"],
            childColumns = ["video_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(
            value = [
                "audio_id",
            ],
        ),
        Index(
            value = [
                "video_id",
            ],
        ),
        Index(
            value = [
                "play_list_id",
            ],
        ),
    ],
)
data class PlayListItemEntryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "play_list_id")
    val playListId: Long,
    @ColumnInfo(name = "audio_id", defaultValue = "NULL")
    val audioId: String? = null,
    @ColumnInfo(name = "video_id", defaultValue = "NULL")
    val videoId: String? = null,
    @ColumnInfo(name = "added_date")
    val addedDate: Long,
) {
    init {
        require(audioId != null || videoId != null) { "Either audioId or videoId must be set" }
        require(audioId == null || videoId == null) { "Only one of audioId or videoId can be set" }
    }
}
