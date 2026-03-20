/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.dao.internal

import androidx.paging.PagingSource
import androidx.room3.Dao
import androidx.room3.DaoReturnTypeConverters
import androidx.room3.RawQuery
import androidx.room3.RoomRawQuery
import androidx.room3.paging.PagingSourceDaoReturnTypeConverter
import com.andannn.melodify.core.database.entity.AudioEntity
import kotlinx.coroutines.flow.Flow

@Dao
@DaoReturnTypeConverters(PagingSourceDaoReturnTypeConverter::class)
internal interface MediaEntityRawQueryDao {
    @RawQuery(observedEntities = [AudioEntity::class])
    fun getMediaFlowRaw(rawQuery: RoomRawQuery): Flow<List<AudioEntity>>

    @RawQuery(observedEntities = [AudioEntity::class])
    fun getMediaFlowPagingSource(rawQuery: RoomRawQuery): PagingSource<Int, AudioEntity>
}
