/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

internal object SearchHistoryColumns {
    const val ID = "search_history_id"
    const val SEARCH_DATE = "search_date"
    const val SEARCH_TEXT = "search_text"
}

@Entity(
    tableName = "search_history_table",
    indices = [
        Index(value = [SearchHistoryColumns.SEARCH_TEXT], unique = true),
    ],
)
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "search_history_id")
    val id: Long = 0,
    @ColumnInfo(name = "search_date")
    val searchDate: Long,
    @ColumnInfo(name = "search_text")
    val searchText: String,
)
