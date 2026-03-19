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
import com.andannn.melodify.core.database.entity.PlayListEntity
import com.andannn.melodify.core.database.entity.PlayListItemEntryEntity
import com.andannn.melodify.core.database.entity.VideoEntity
import com.andannn.melodify.core.database.model.AudioVideoMergedResult
import kotlinx.coroutines.flow.Flow

@Dao
@DaoReturnTypeConverters(PagingSourceDaoReturnTypeConverter::class)
interface PlayListRawQueryDao {
    @RawQuery(observedEntities = [AudioEntity::class, VideoEntity::class, PlayListEntity::class, PlayListItemEntryEntity::class])
    fun getMediasInPlayListFlowRaw(rawQuery: RoomRawQuery): Flow<List<AudioVideoMergedResult>>

    @RawQuery(observedEntities = [AudioEntity::class, VideoEntity::class, PlayListEntity::class, PlayListItemEntryEntity::class])
    fun getMediasInPlayListFlowPagingSource(rawQuery: RoomRawQuery): PagingSource<Int, AudioVideoMergedResult>
}
