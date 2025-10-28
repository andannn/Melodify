/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.andannn.melodify.core.database.Tables
import com.andannn.melodify.core.database.entity.CustomTabColumns
import com.andannn.melodify.core.database.entity.CustomTabEntity
import com.andannn.melodify.core.database.entity.SearchHistoryColumns
import com.andannn.melodify.core.database.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDataDao {
    @Query("SELECT * FROM ${Tables.CUSTOM_TAB} ORDER BY ${CustomTabColumns.SORT_ORDER} ASC")
    fun getCustomTabsFlow(): Flow<List<CustomTabEntity>>

    @Query("DELETE FROM ${Tables.CUSTOM_TAB}")
    suspend fun deleteAllCustomTabs()

    @Query("SELECT MAX(${CustomTabColumns.SORT_ORDER}) FROM ${Tables.CUSTOM_TAB}")
    suspend fun getMaxSortOrder(): Int?

    @Query("SELECT ${CustomTabColumns.SORT_ORDER} FROM ${Tables.CUSTOM_TAB} WHERE ${CustomTabColumns.ID} = :id LIMIT 1")
    suspend fun getSortOrder(id: Long): Int?

    @Query("UPDATE ${Tables.CUSTOM_TAB} SET ${CustomTabColumns.SORT_ORDER} = :sortOrder WHERE ${CustomTabColumns.ID} = :id")
    suspend fun updateSortOrder(
        id: Long,
        sortOrder: Int,
    )

    @Transaction
    suspend fun swapTabOrder(
        fromId: Long,
        toId: Long,
    ) {
        val fromOrder = getSortOrder(fromId) ?: 0
        val toOrder = getSortOrder(toId) ?: 0

        updateSortOrder(fromId, toOrder)
        updateSortOrder(toId, fromOrder)
    }

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCustomTab(customTab: CustomTabEntity): Long?

    @Query("DELETE FROM ${Tables.CUSTOM_TAB} WHERE ${CustomTabColumns.ID} = :tabId")
    suspend fun deleteCustomTab(tabId: Long)

    @Query(
        """
        SELECT EXISTS(
            SELECT 1
            FROM ${Tables.CUSTOM_TAB}
            WHERE ${CustomTabColumns.EXTERNAL_ID} = :externalId
            AND ${CustomTabColumns.NAME} = :name
            AND ${CustomTabColumns.TYPE} = :type
        )
    """,
    )
    suspend fun isTabExist(
        externalId: String,
        name: String,
        type: String,
    ): Boolean

    @Query("UPDATE ${Tables.CUSTOM_TAB} SET ${CustomTabColumns.DISPLAY_SETTING} = :settings WHERE custom_tab_id == :tabId")
    suspend fun updateDisplaySettingForTab(
        tabId: Long,
        settings: String,
    ): Int

    @Query(
        "SELECT ${Tables.CUSTOM_TAB}.${CustomTabColumns.DISPLAY_SETTING} FROM ${Tables.CUSTOM_TAB} WHERE ${CustomTabColumns.ID} = :tabId",
    )
    fun getDisplaySettingFlowOfTab(tabId: Long): Flow<String?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSearchHistory(searchHistories: List<SearchHistoryEntity>)

    @Query("SELECT * FROM ${Tables.SEARCH_HISTORY} ORDER BY ${SearchHistoryColumns.SEARCH_DATE} DESC LIMIT :limit")
    suspend fun getSearchHistories(limit: Int): List<SearchHistoryEntity>
}
