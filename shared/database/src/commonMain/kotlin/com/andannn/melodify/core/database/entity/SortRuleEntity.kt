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
import com.andannn.melodify.core.database.Tables

@Entity(
    tableName = Tables.SORT_RULE,
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
data class SortRuleEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "sort_rule_id")
    val id: Long = 0,
    @ColumnInfo("custom_tab_foreign_key")
    val foreignKey: Long,
    @ColumnInfo("primary_group_sort")
    val primaryGroupSort: SortOptionData? = null,
    @ColumnInfo("secondary_group_sort")
    val secondaryGroupSort: SortOptionData? = null,
    @ColumnInfo("content_sort")
    val contentSort: SortOptionData? = null,
    @ColumnInfo("show_track_num")
    val showTrackNum: Boolean = false,
    @ColumnInfo("is_preset")
    val isPreset: Boolean = true,
)

data class SortOptionData(
    val type: Int,
    val isAscending: Boolean,
) {
    companion object {
        // Audio
        const val SORT_TYPE_AUDIO_ALBUM = 1
        const val SORT_TYPE_AUDIO_ARTIST = 2
        const val SORT_TYPE_AUDIO_GENRE = 3
        const val SORT_TYPE_AUDIO_TITLE = 4
        const val SORT_TYPE_AUDIO_YEAR = 5
        const val SORT_TYPE_AUDIO_TRACK_NUM = 6

        // Video
        const val SORT_TYPE_VIDEO_BUCKET_NAME = 7
        const val SORT_TYPE_VIDEO_TITLE_NAME = 8
    }
}

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
