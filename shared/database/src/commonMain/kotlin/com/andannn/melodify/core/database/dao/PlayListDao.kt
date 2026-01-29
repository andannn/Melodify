/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.andannn.melodify.core.database.entity.PlayListEntity
import com.andannn.melodify.core.database.entity.PlayListItemEntryEntity
import com.andannn.melodify.core.database.model.PlayListWithMediaCount
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayListDao {
    @Query(
        """
        SELECT p.*, COUNT(e.id) AS mediaCount
        FROM play_list_table AS p
        LEFT JOIN play_list_item_entry_table AS e
            ON p.play_list_id = e.play_list_id
        GROUP BY p.play_list_id
        ORDER BY e.added_date DESC
    """,
    )
    fun getAllPlayListFlow(): Flow<List<PlayListWithMediaCount>>

    @Query(
        """
        SELECT p.*, COUNT(e.id) AS mediaCount
        FROM play_list_table AS p
        LEFT JOIN play_list_item_entry_table AS e
            ON p.play_list_id = e.play_list_id
        WHERE p.play_list_id = :playListId
        GROUP BY p.play_list_id
    """,
    )
    fun getPlayListFlowById(playListId: Long): Flow<PlayListWithMediaCount?>

    @Insert(entity = PlayListEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlayListEntities(entities: List<PlayListEntity>): List<Long>

    @Insert(entity = PlayListItemEntryEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlayListWithMediaCrossRef(crossRefs: List<PlayListItemEntryEntity>): List<Long>

    @Query(
        """
        SELECT play_list_with_media_cross_ref_media_store_id
        FROM play_list_with_media_cross_ref_table
        WHERE play_list_with_media_cross_ref_play_list_id = :playListId AND
            play_list_with_media_cross_ref_media_store_id IN (:mediaIdList)
    """,
    )
    suspend fun getDuplicateMediaInPlayList(
        playListId: Long,
        mediaIdList: List<String>,
    ): List<String>

    @Query(
        """
            DELETE FROM play_list_with_media_cross_ref_table
            WHERE play_list_with_media_cross_ref_play_list_id = :playListId AND
                play_list_with_media_cross_ref_media_store_id IN (:mediaIdList)
    """,
    )
    suspend fun deleteMediaFromPlayList(
        playListId: Long,
        mediaIdList: List<String>,
    )

    @Query(
        """
        SELECT * FROM play_list_table
        WHERE play_list_id = :playListId
    """,
    )
    suspend fun getPlayListEntity(playListId: Long): PlayListEntity?

    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM play_list_with_media_cross_ref_table
            WHERE play_list_with_media_cross_ref_play_list_id = :playList AND
                play_list_with_media_cross_ref_media_store_id = :mediaStoreId
        )
    """,
    )
    fun getIsMediaInPlayListFlow(
        playList: String,
        mediaStoreId: String,
    ): Flow<Boolean>

    @Query(
        """
        SELECT * FROM play_list_table
        WHERE is_favorite_playlist = 1
    """,
    )
    fun getFavoritePlayListFlow(): Flow<PlayListEntity?>

    @Query(
        """
        delete from play_list_table
        where play_list_id = :playListId
    """,
    )
    suspend fun deletePlayListById(playListId: Long)
}
