/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.dao.internal

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.RawQuery
import androidx.room.RoomRawQuery
import com.andannn.melodify.core.database.entity.AudioEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface MediaEntityRawQueryDao {
    @RawQuery(observedEntities = [AudioEntity::class])
    fun getMediaFlowRaw(rawQuery: RoomRawQuery): Flow<List<AudioEntity>>

    @RawQuery(observedEntities = [AudioEntity::class])
    fun getMediaFlowPagingSource(rawQuery: RoomRawQuery): PagingSource<Int, AudioEntity>
}
