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
data class TabCustomSortRuleEntity(
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

        // PlayList
        const val SORT_TYPE_PLAYLIST_CREATE_DATE = 9
    }
}
