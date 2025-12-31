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

@Entity(
    tableName = Tables.VIDEO_TAB_SETTING,
    foreignKeys = [
        ForeignKey(
            entity = CustomTabEntity::class,
            parentColumns = [CustomTabColumns.ID],
            childColumns = ["custom_tab_foreign_key"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(
            "custom_tab_foreign_key",
            unique = true,
        ),
    ],
)
data class VideoTabSettingEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo("custom_tab_foreign_key")
    val foreignKey: Long,
    @ColumnInfo("is_show_progress")
    val isShowProgress: Boolean = false,
)
