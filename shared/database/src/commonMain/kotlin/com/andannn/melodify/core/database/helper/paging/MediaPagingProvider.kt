/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.helper.paging

import androidx.paging.PagingSource
import com.andannn.melodify.core.database.dao.internal.MediaEntityRawQueryDao
import com.andannn.melodify.core.database.entity.AudioEntity
import kotlinx.coroutines.flow.Flow

internal class MediaPagingProvider constructor(
    private val provider: MediaEntityRawQueryDao,
    private val extraWhere: List<Where>,
) : PagingProvider<AudioEntity> {
    override fun getDataFlow(
        where: MediaWheres?,
        sort: MediaSorts?,
    ): Flow<List<AudioEntity>> =
        provider.getMediaFlowRaw(
            buildMediaRawQuery(
                wheres =
                    where.appendOrCreateWith {
                        listOf(
                            *extraWhere.toTypedArray(),
                            MediaEntityWhere.audioNotDeletedWhere(),
                        )
                    },
                sort = sort,
            ),
        )

    override fun getPagingSource(
        where: MediaWheres?,
        sort: MediaSorts?,
    ): PagingSource<Int, AudioEntity> =
        provider.getMediaFlowPagingSource(
            buildMediaRawQuery(
                wheres =
                    where.appendOrCreateWith {
                        listOf(
                            *extraWhere.toTypedArray(),
                            MediaEntityWhere.audioNotDeletedWhere(),
                        )
                    },
                sort = sort,
            ),
        )
}
