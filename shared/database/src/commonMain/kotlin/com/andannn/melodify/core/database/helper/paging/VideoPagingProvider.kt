/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.helper.paging

import androidx.paging.PagingSource
import com.andannn.melodify.core.database.dao.internal.VideoEntityRawQueryDao
import com.andannn.melodify.core.database.entity.VideoEntity
import kotlinx.coroutines.flow.Flow

internal class VideoPagingProvider(
    private val provider: VideoEntityRawQueryDao,
    private val extraWhere: List<Where>,
) : PagingProvider<VideoEntity> {
    override fun getDataFlow(
        where: MediaWheres?,
        sort: MediaSorts?,
    ): Flow<List<VideoEntity>> =
        provider.getVideoFlowRaw(
            buildVideoRawQuery(
                wheres =
                    where.appendOrCreateWith {
                        listOf(
                            *extraWhere.toTypedArray(),
                            VideoEntityWhere.videoNotDeletedWhere(),
                        )
                    },
                sort = sort,
            ),
        )

    override fun getPagingSource(
        where: MediaWheres?,
        sort: MediaSorts?,
    ): PagingSource<Int, VideoEntity> =
        provider.getVideoFlowPagingSource(
            buildVideoRawQuery(
                wheres =
                    where.appendOrCreateWith {
                        listOf(
                            *extraWhere.toTypedArray(),
                            VideoEntityWhere.videoNotDeletedWhere(),
                        )
                    },
                sort = sort,
            ),
        )
}
