/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.helper.paging

import androidx.paging.PagingSource
import com.andannn.melodify.core.database.dao.internal.MediaEntityRawQueryDao
import com.andannn.melodify.core.database.entity.MediaEntity
import kotlinx.coroutines.flow.Flow

internal class AllMediaPagingProvider(
    private val provider: MediaEntityRawQueryDao,
) : PagingProvider<MediaEntity> {
    override fun getDataFlow(
        where: MediaWheres?,
        sort: MediaSorts?,
    ): Flow<List<MediaEntity>> =
        provider.getMediaFlowRaw(
            buildMediaRawQuery(
                wheres =
                    where.appendOrCreateWith {
                        listOf(
                            MediaEntityWhere.audioNotDeletedWhere(),
                        )
                    },
                sort = sort,
            ),
        )

    override fun getPagingSource(
        where: MediaWheres?,
        sort: MediaSorts?,
    ): PagingSource<Int, MediaEntity> =
        provider.getMediaFlowPagingSource(
            buildMediaRawQuery(
                wheres =
                    where.appendOrCreateWith {
                        listOf(
                            MediaEntityWhere.audioNotDeletedWhere(),
                        )
                    },
                sort = sort,
            ),
        )
}
