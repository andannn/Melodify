/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.dao.internal

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.RawQuery
import androidx.room.RoomRawQuery
import com.andannn.melodify.core.database.entity.MediaEntity
import com.andannn.melodify.core.database.entity.PlayListEntity
import com.andannn.melodify.core.database.entity.PlayListItemEntryEntity
import com.andannn.melodify.core.database.entity.VideoEntity
import com.andannn.melodify.core.database.model.AudioVideoMergedResult
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayListRawQueryDao {
    @RawQuery(observedEntities = [MediaEntity::class, VideoEntity::class, PlayListEntity::class, PlayListItemEntryEntity::class])
    fun getMediasInPlayListFlowRaw(rawQuery: RoomRawQuery): Flow<List<AudioVideoMergedResult>>

    @RawQuery(observedEntities = [MediaEntity::class, VideoEntity::class, PlayListEntity::class, PlayListItemEntryEntity::class])
    fun getMediasInPlayListFlowPagingSource(rawQuery: RoomRawQuery): PagingSource<Int, AudioVideoMergedResult>
}
