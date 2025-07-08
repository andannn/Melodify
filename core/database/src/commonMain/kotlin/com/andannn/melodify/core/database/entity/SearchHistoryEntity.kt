/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.andannn.melodify.core.database.Tables

internal object SearchHistoryColumns {
    const val ID = "search_history_id"
    const val SEARCH_DATE = "search_date"
    const val SEARCH_TEXT = "search_text"
}

@Entity(
    tableName = Tables.SEARCH_HISTORY,
    indices = [
        Index(value = [SearchHistoryColumns.SEARCH_TEXT], unique = true),
    ],
)
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = SearchHistoryColumns.ID)
    val id: Long = 0,
    @ColumnInfo(name = SearchHistoryColumns.SEARCH_DATE)
    val searchDate: Long,
    @ColumnInfo(name = SearchHistoryColumns.SEARCH_TEXT)
    val searchText: String,
)
