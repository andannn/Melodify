/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey

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
