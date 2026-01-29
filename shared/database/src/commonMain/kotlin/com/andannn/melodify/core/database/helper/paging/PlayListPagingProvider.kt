/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.helper.paging

import androidx.paging.PagingSource
import androidx.room.RoomRawQuery
import com.andannn.melodify.core.database.dao.internal.PlayListRawQueryDao
import com.andannn.melodify.core.database.model.AudioVideoMergedResult
import kotlinx.coroutines.flow.Flow

internal class PlayListPagingProvider(
    private val playListId: Long,
    private val provider: PlayListRawQueryDao,
) : PagingProvider<AudioVideoMergedResult> {
    override fun getDataFlow(
        where: MediaWheres?,
        sort: MediaSorts?,
    ): Flow<List<AudioVideoMergedResult>> =
        provider.getMediasInPlayListFlowRaw(
            buildPlayListRawQuery(
                where.appendOrCreateWith {
                    listOf(
                        playListIdWhere(playListId.toString()),
                    )
                },
                sort,
            ),
        )

    override fun getPagingSource(
        where: MediaWheres?,
        sort: MediaSorts?,
    ): PagingSource<Int, AudioVideoMergedResult> =
        provider.getMediasInPlayListFlowPagingSource(
            buildVideoRawQuery(
                where.appendOrCreateWith {
                    listOf(
                        playListIdWhere(playListId.toString()),
                    )
                },
                sort,
            ),
        )
}

private fun playListIdWhere(playListId: String) =
    Where(
        "play_list_id",
        "=",
        playListId,
    )

private fun buildPlayListRawQuery(
    wheres: MediaWheres?,
    sort: MediaSorts?,
): RoomRawQuery {
    val sql = """
            SELECT a.*, v.* 
            FROM play_list_table AS p
            JOIN play_list_item_entry_table AS e
                ON p.play_list_id = e.play_list_id
            LEFT JOIN library_media_table AS a
                ON a.media_id = e.audio_id AND a.deleted = 0
            LEFT JOIN library_video_table AS v AND v.video_deleted = 0
                ON v.video_id = e.video_id
            ${wheres.toWhereString()}
            ${sort.toSortString()}
        """
    return RoomRawQuery(sql)
}
