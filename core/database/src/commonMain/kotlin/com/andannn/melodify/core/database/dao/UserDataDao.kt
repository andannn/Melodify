package com.andannn.melodify.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.andannn.melodify.core.database.Tables
import com.andannn.melodify.core.database.entity.CustomTabEntity
import com.andannn.melodify.core.database.entity.SearchHistoryColumns
import com.andannn.melodify.core.database.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDataDao {

    @Query("SELECT * FROM ${Tables.CUSTOM_TAB}")
    fun getCustomTabsFlow(): Flow<List<CustomTabEntity>>

    @Query("DELETE FROM ${Tables.CUSTOM_TAB}")
    suspend fun deleteAllCustomTabs()

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCustomTabs(customTabs: List<CustomTabEntity>)

    @Transaction
    suspend fun clearAndInsertCustomTabs(customTabs: List<CustomTabEntity>) {
        deleteAllCustomTabs()
        insertCustomTabs(customTabs)
    }

    @Query("DELETE FROM ${Tables.CUSTOM_TAB} WHERE custom_tab_external_id = :externalId AND custom_tab_type = :type")
    suspend fun deleteCustomTab(externalId: String?, type: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSearchHistory(searchHistories: List<SearchHistoryEntity>)

    @Query("SELECT * FROM ${Tables.SEARCH_HISTORY} ORDER BY ${SearchHistoryColumns.SEARCH_DATE} DESC LIMIT :limit")
    suspend fun getSearchHistories(limit: Int): List<SearchHistoryEntity>
}