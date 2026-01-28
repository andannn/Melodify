/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.andannn.melodify.core.database.entity.LyricColumns
import com.andannn.melodify.core.database.entity.LyricEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LyricDao {
    @Insert(entity = LyricEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLyricEntities(entities: List<LyricEntity>)

    @Query(
        """
        SELECT * FROM lyric_table WHERE :mediaStoreId = ${LyricColumns.MEDIA_ID}
    """,
    )
    fun getLyricByMediaIdFlow(mediaStoreId: String): Flow<LyricEntity?>
}
