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
import com.andannn.melodify.core.database.Tables

internal object VideoPlayProgressColumns {
    const val ID = "id"
    const val EXTERNAL_VIDEO_ID = "external_video_id"
    const val PROGRESS_MS = "progress"
    const val IS_FINISHED = "is_finished"
}

@Entity(
    tableName = Tables.VIDEO_PLAY_PROGRESS,
    foreignKeys = [
        ForeignKey(
            entity = VideoEntity::class,
            parentColumns = [VideoColumns.ID],
            childColumns = [VideoPlayProgressColumns.EXTERNAL_VIDEO_ID],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(
            "external_video_id",
            unique = true,
        ),
    ],
)
class VideoPlayProgressEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = VideoPlayProgressColumns.ID)
    val id: Long = 0,
    @ColumnInfo(name = VideoPlayProgressColumns.EXTERNAL_VIDEO_ID)
    val externalVideoId: String,
    /** The progress in milliseconds. If the video is finished, this value is [FINISHED_MS]. */
    @ColumnInfo(name = VideoPlayProgressColumns.PROGRESS_MS)
    val progressMs: Long,
    @ColumnInfo(name = VideoPlayProgressColumns.IS_FINISHED)
    val isFinished: Boolean? = false,
) {
    fun getIsVideoFinished(): Boolean = isFinished == true
}
