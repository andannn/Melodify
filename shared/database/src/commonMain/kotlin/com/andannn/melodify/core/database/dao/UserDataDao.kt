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
import com.andannn.melodify.core.database.entity.CustomTabColumns
import com.andannn.melodify.core.database.entity.CustomTabEntity
import com.andannn.melodify.core.database.entity.SearchHistoryColumns
import com.andannn.melodify.core.database.entity.SearchHistoryEntity
import com.andannn.melodify.core.database.entity.SortRuleEntity
import com.andannn.melodify.core.database.entity.VideoPlayProgressColumns
import com.andannn.melodify.core.database.entity.VideoPlayProgressEntity
import com.andannn.melodify.core.database.entity.VideoTabSettingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDataDao {
    @Query("SELECT * FROM custom_tab_table ORDER BY ${CustomTabColumns.SORT_ORDER} ASC")
    fun getCustomTabsFlow(): Flow<List<CustomTabEntity>>

    @Query("DELETE FROM custom_tab_table")
    suspend fun deleteAllCustomTabs()

    @Query("SELECT MAX(${CustomTabColumns.SORT_ORDER}) FROM custom_tab_table")
    suspend fun getMaxSortOrder(): Int?

    @Query("SELECT ${CustomTabColumns.SORT_ORDER} FROM custom_tab_table WHERE ${CustomTabColumns.ID} = :id LIMIT 1")
    suspend fun getSortOrder(id: Long): Int

    @Transaction
    suspend fun swapTabOrder(
        fromId: Long,
        toId: Long,
    ) {
        val fromOrder = getSortOrder(fromId)
        val toOrder = getSortOrder(toId)

        moveByOrder(
            from = fromOrder,
            to = toOrder,
            movingId = fromId,
        )
    }

    @Query(
        """
        UPDATE custom_tab_table
        SET sort_order = CASE
            WHEN :from = :to THEN sort_order
            WHEN :movingId IS NOT NULL AND custom_tab_id = :movingId THEN :to
            WHEN :from < :to AND sort_order > :from AND sort_order <= :to
                THEN sort_order - 1
            WHEN :from > :to AND sort_order >= :to AND sort_order < :from
                THEN sort_order + 1
            ELSE sort_order
        END
        WHERE (sort_order BETWEEN MIN(:from, :to) AND MAX(:from, :to))
           OR (custom_tab_id = :movingId)
        """,
    )
    suspend fun moveByOrder(
        from: Int,
        to: Int,
        movingId: Long,
    )

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCustomTab(customTab: CustomTabEntity): Long?

    @Query("DELETE FROM custom_tab_table WHERE ${CustomTabColumns.ID} = :tabId")
    suspend fun deleteCustomTab(tabId: Long)

    @Query(
        """
        SELECT EXISTS(
            SELECT 1
            FROM custom_tab_table
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

    @Query(
        """
        SELECT EXISTS(
            SELECT 1
            FROM custom_tab_table
            WHERE ${CustomTabColumns.TYPE} = :type
        )
    """,
    )
    suspend fun isTabKindExist(type: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSortRuleEntity(entity: SortRuleEntity)

    @Query("SELECT * FROM sort_rule_table WHERE custom_tab_foreign_key = :tabId")
    fun getDisplaySettingFlowOfTab(tabId: Long): Flow<SortRuleEntity?>

    @Query(
        """
    INSERT INTO video_tab_setting_table (
        custom_tab_foreign_key, 
        is_show_progress
    ) VALUES (
        :tabId,
        :isShowProgress
    ) ON CONFLICT(custom_tab_foreign_key) DO UPDATE SET 
        is_show_progress = excluded.is_show_progress
    """,
    )
    suspend fun upsertVideoTabSettingEntity(
        tabId: Long,
        isShowProgress: Boolean,
    )

    @Query("SELECT * FROM video_tab_setting_table WHERE custom_tab_foreign_key = :tabId")
    fun getVideoSettingFlowOfTab(tabId: Long): Flow<VideoTabSettingEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSearchHistory(searchHistories: List<SearchHistoryEntity>)

    @Query("SELECT * FROM search_history_table ORDER BY ${SearchHistoryColumns.SEARCH_DATE} DESC LIMIT :limit")
    suspend fun getSearchHistories(limit: Int): List<SearchHistoryEntity>

    @Query("UPDATE video_play_progress_table SET is_finished = 1 WHERE ${VideoPlayProgressColumns.EXTERNAL_VIDEO_ID} = :videoId")
    suspend fun markVideoAsWatched(videoId: Long): Int

    @Query(
        """
    INSERT INTO video_play_progress_table (
        external_video_id, 
        progress
    ) VALUES (
        :videoId,
        :progressMs
    ) ON CONFLICT(external_video_id) DO UPDATE SET 
        progress = excluded.progress
    """,
    )
    suspend fun savePlayProgress(
        videoId: Long,
        progressMs: Long,
    )

    @Query("SELECT * FROM video_play_progress_table WHERE external_video_id = :videoId")
    fun getPlayProgressFlow(videoId: Long): Flow<VideoPlayProgressEntity?>
}
