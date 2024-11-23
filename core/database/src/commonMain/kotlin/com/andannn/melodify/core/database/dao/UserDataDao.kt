package com.andannn.melodify.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.andannn.melodify.core.database.Tables
import com.andannn.melodify.core.database.entity.CustomTabEntity
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
}