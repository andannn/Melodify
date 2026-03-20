/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.andannn.melodify.core.database.entity.LyricEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LyricDao {
    @Insert(entity = LyricEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLyricEntities(entities: List<LyricEntity>)

    @Query(
        """
        SELECT * FROM lyric_table WHERE :mediaStoreId = lyric_table.media_id
    """,
    )
    fun getLyricByMediaIdFlow(mediaStoreId: Long): Flow<LyricEntity?>
}
