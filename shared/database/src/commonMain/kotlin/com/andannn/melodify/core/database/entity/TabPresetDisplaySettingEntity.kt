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
    tableName = "tab_preset_display_setting_table",
    foreignKeys = [
        ForeignKey(
            entity = TabEntity::class,
            parentColumns = ["custom_tab_id"],
            childColumns = ["tab_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(
            "tab_id",
            unique = true,
        ),
    ],
)
data class TabPresetDisplaySettingEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo("tab_id")
    val customTabId: Long,
    @ColumnInfo("selected_preset_display_setting", defaultValue = "NULL")
    val preset: Int? = null,
)
