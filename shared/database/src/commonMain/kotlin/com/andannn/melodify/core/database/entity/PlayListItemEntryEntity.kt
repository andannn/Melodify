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
        Index(
            value = [
                "entry_type",
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
    val audioId: Long? = null,
    @ColumnInfo(name = "video_id", defaultValue = "NULL")
    val videoId: Long? = null,
    /**
     * 0 for audio, 1 for video
     */
    @ColumnInfo(name = "entry_type")
    val entryType: Long,
    @ColumnInfo(name = "added_date")
    val addedDate: Long,
) {
    constructor(
        playListId: Long,
        audioId: Long? = null,
        videoId: Long? = null,
        addedDate: Long,
    ) : this(
        playListId = playListId,
        audioId = audioId,
        videoId = videoId,
        entryType = if (audioId != null) PlayListEntryType.AUDIO else PlayListEntryType.VIDEO,
        addedDate = addedDate,
    )

    init {
        require(audioId != null || videoId != null) { "Either audioId or videoId must be set" }
        require(audioId == null || videoId == null) { "Only one of audioId or videoId can be set" }
    }
}

object PlayListEntryType {
    const val AUDIO = 0L
    const val VIDEO = 1L
}
