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
    tableName = "custom_tab_setting_table",
    foreignKeys = [
        ForeignKey(
            entity = CustomTabEntity::class,
            parentColumns = ["custom_tab_id"],
            childColumns = ["custom_tab_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(
            "custom_tab_id",
            unique = true,
        ),
    ],
)
data class CustomTabSettingEntity constructor(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo("custom_tab_id")
    val customTabId: Long,
    @ColumnInfo("is_show_video_progress", defaultValue = "0")
    val isShowVideoProgress: Boolean = false,
    /** 0: album cover, 1: track number */
    @ColumnInfo("audio_entry_style", defaultValue = "0")
    val audioEntryStyle: Long,
)

object AudioEntryStyle {
    const val ALBUM_COVER = 0L
    const val TRACK_NUMBER = 1L
}
