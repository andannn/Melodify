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
import com.andannn.melodify.core.database.entity.PlayListEntity
import com.andannn.melodify.core.database.entity.PlayListEntryType
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
        ORDER BY p.play_list_created_date DESC
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

    /**
     * Delete media from a play list.
     *
     * @param mediaList pair of media id and entry type.
     */
    @Transaction
    suspend fun deleteMediasFromPlayList(
        playListId: Long,
        mediaList: List<Pair<Long, Long>>,
    ) {
        mediaList.forEach { (mediaId, entryType) ->
            deleteMediaFromPlayList(playListId, mediaId, entryType)
        }
    }

    /**
     * Delete media from a play list.
     *
     * @param playListId The play list id.
     * @param mediaId The media id.
     * @param entryType [com.andannn.melodify.core.database.entity.PlayListEntryType] The entry type.
     */
    @Query(
        """
            DELETE FROM play_list_item_entry_table
            WHERE play_list_id = :playListId AND (
                (:entryType = 0 AND audio_id = :mediaId) 
                OR 
                (:entryType = 1 AND video_id = :mediaId)
            )
    """,
    )
    suspend fun deleteMediaFromPlayList(
        playListId: Long,
        mediaId: Long,
        entryType: Long,
    )

    @Query(
        """
        SELECT * FROM play_list_table
        WHERE play_list_id = :playListId
    """,
    )
    suspend fun getPlayListEntity(playListId: Long): PlayListEntity?

    /**
     * Check if a media is in a play list.
     *
     * @param playList The play list id.
     * @param mediaId The media id.
     * @param entryType [com.andannn.melodify.core.database.entity.PlayListEntryType] The entry type.
     */
    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM play_list_item_entry_table AS p
            WHERE p.play_list_id = :playList AND (
                (:entryType = 0 AND p.audio_id = :mediaId) 
                OR 
                (:entryType = 1 AND p.video_id = :mediaId)
            )
        )
    """,
    )
    fun getIsMediaInPlayListFlow(
        playList: Long,
        entryType: Long,
        mediaId: Long,
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
