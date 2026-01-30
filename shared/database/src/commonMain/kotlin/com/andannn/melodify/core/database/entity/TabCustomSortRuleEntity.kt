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
import androidx.room.TypeConverter
import com.andannn.melodify.core.database.SortOptionData

@Entity(
    tableName = "sort_rule_table",
    foreignKeys = [
        ForeignKey(
            entity = TabEntity::class,
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
data class CustomTabSortRuleEntity constructor(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "sort_rule_id")
    val id: Long = 0,
    @ColumnInfo("custom_tab_id")
    val foreignKey: Long,
    @ColumnInfo("primary_group_sort")
    val primaryGroupSort: SortOptionData? = null,
    @ColumnInfo("secondary_group_sort")
    val secondaryGroupSort: SortOptionData? = null,
    @ColumnInfo("content_sort")
    val contentSort: SortOptionData? = null,
    @ColumnInfo("is_preset")
    val isPreset: Boolean = true,
)

internal class SortOptionJsonConverter {
    @TypeConverter
    fun from(value: String?): SortOptionData? =
        value?.let {
            val (type, isAscending) = it.split(",")
            SortOptionData(
                type = type.toInt(),
                isAscending = isAscending.toBoolean(),
            )
        }

    @TypeConverter
    fun to(date: SortOptionData?): String? =
        date?.let {
            listOf(it.type.toString(), it.isAscending.toString()).joinToString(",")
        }
}
