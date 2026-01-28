/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.RoomRawQuery
import com.andannn.melodify.core.database.entity.MediaEntity
import com.andannn.melodify.core.database.entity.PlayListEntity
import com.andannn.melodify.core.database.entity.PlayListWithMediaCrossRef
import com.andannn.melodify.core.database.entity.VideoEntity
import com.andannn.melodify.core.database.helper.paging.MediaSorts
import com.andannn.melodify.core.database.helper.paging.MediaWheres
import com.andannn.melodify.core.database.helper.paging.Where
import com.andannn.melodify.core.database.helper.paging.appendOrCreateWith
import com.andannn.melodify.core.database.helper.paging.toSortString
import com.andannn.melodify.core.database.helper.paging.toWhereString
import com.andannn.melodify.core.database.model.CrossRefWithMediaRelation
import com.andannn.melodify.core.database.model.CrossRefWithVideoRelation
import com.andannn.melodify.core.database.model.PlayListWithMediaCount
import kotlinx.coroutines.flow.Flow

private const val PLAYLIST_WITH_COUNT_SELECTION =
    """
        SELECT play_list_table.*, COUNT(play_list_with_media_cross_ref_media_store_id) AS mediaCount
        FROM play_list_table
        LEFT JOIN play_list_with_media_cross_ref_table
            ON play_list_id = play_list_with_media_cross_ref_play_list_id
    """

@Dao
interface PlayListDao {
    @Query(
        """
        $PLAYLIST_WITH_COUNT_SELECTION
        WHERE is_audio_playlist = :isAudio
        GROUP BY play_list_id
        ORDER BY play_list_created_date DESC
    """,
    )
    fun getAllPlayListFlow(isAudio: Boolean): Flow<List<PlayListWithMediaCount>>

    @Query(
        """
        $PLAYLIST_WITH_COUNT_SELECTION
        GROUP BY play_list_id
        ORDER BY play_list_created_date DESC
    """,
    )
    fun getAllPlayListFlow(): Flow<List<PlayListWithMediaCount>>

    @Insert(entity = PlayListEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlayListEntities(entities: List<PlayListEntity>): List<Long>

    @Insert(entity = PlayListWithMediaCrossRef::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlayListWithMediaCrossRef(crossRefs: List<PlayListWithMediaCrossRef>): List<Long>

    @Query(
        """
        SELECT play_list_with_media_cross_ref_media_store_id
        FROM play_list_with_media_cross_ref_table
        WHERE play_list_with_media_cross_ref_play_list_id = :playListId AND
            play_list_with_media_cross_ref_media_store_id IN (:mediaIdList)
    """,
    )
    suspend fun getDuplicateMediaInPlayList(
        playListId: Long,
        mediaIdList: List<String>,
    ): List<String>

    @Query(
        """
            DELETE FROM play_list_with_media_cross_ref_table
            WHERE play_list_with_media_cross_ref_play_list_id = :playListId AND
                play_list_with_media_cross_ref_media_store_id IN (:mediaIdList)
    """,
    )
    suspend fun deleteMediaFromPlayList(
        playListId: Long,
        mediaIdList: List<String>,
    )

    @Query(
        """
        $PLAYLIST_WITH_COUNT_SELECTION
        WHERE play_list_id = :playListId
        GROUP BY play_list_id
    """,
    )
    fun getPlayListFlowById(playListId: Long): Flow<PlayListWithMediaCount?>

    @Query(
        """
        SELECT * FROM play_list_table
        WHERE play_list_id = :playListId
    """,
    )
    suspend fun getPlayListEntity(playListId: Long): PlayListEntity?

    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM play_list_with_media_cross_ref_table
            WHERE play_list_with_media_cross_ref_play_list_id = :playList AND
                play_list_with_media_cross_ref_media_store_id = :mediaStoreId
        )
    """,
    )
    fun getIsMediaInPlayListFlow(
        playList: String,
        mediaStoreId: String,
    ): Flow<Boolean>

    fun getFavoritePlayListFlow(isAudio: Boolean): Flow<PlayListEntity?> =
        if (isAudio) getFavoriteAudioPlayListFlow() else getFavoriteVideoPlayListFlow()

    @Query(
        """
        SELECT * FROM play_list_table
        WHERE is_favorite_playlist = 1 AND is_audio_playlist = 1
    """,
    )
    fun getFavoriteAudioPlayListFlow(): Flow<PlayListEntity?>

    @Query(
        """
        SELECT * FROM play_list_table
        WHERE is_favorite_playlist = 1 AND is_audio_playlist = 0
    """,
    )
    fun getFavoriteVideoPlayListFlow(): Flow<PlayListEntity?>

    @Query(
        """
        delete from play_list_table
        where play_list_id = :playListId
    """,
    )
    suspend fun deletePlayListById(playListId: Long)

    fun getMediasInPlayListFlow(
        playListId: Long,
        wheres: MediaWheres?,
        mediaSorts: MediaSorts?,
    ): Flow<List<CrossRefWithMediaRelation>> =
        getMediasInPlayListFlowRaw(
            buildPlayListRawQuery(
                wheres.appendOrCreateWith {
                    listOf(
                        playListIdWhere(playListId.toString()),
                        audioNotDeletedWhere(),
                    )
                },
                mediaSorts,
            ),
        )

    fun getVideosInPlayListFlow(
        playListId: Long,
        wheres: MediaWheres?,
        mediaSorts: MediaSorts?,
    ): Flow<List<CrossRefWithVideoRelation>> =
        getVideosInPlayListFlowRaw(
            buildVideoRawQuery(
                wheres.appendOrCreateWith {
                    listOf(
                        playListIdWhere(playListId.toString()),
                        videoNotDeletedWhere(),
                    )
                },
                mediaSorts,
            ),
        )

    fun getMediaPagingSourceInPlayList(
        playListId: Long,
        wheres: MediaWheres?,
        mediaSorts: MediaSorts? = null,
    ): PagingSource<Int, CrossRefWithMediaRelation> =
        getMediasInPlayListFlowPagingSource(
            buildPlayListRawQuery(
                wheres.appendOrCreateWith {
                    listOf(
                        playListIdWhere(playListId.toString()),
                        audioNotDeletedWhere(),
                    )
                },
                mediaSorts,
            ),
        )

    fun getVideoPagingSourceInPlayList(
        playListId: Long,
        wheres: MediaWheres?,
        mediaSorts: MediaSorts? = null,
    ): PagingSource<Int, CrossRefWithVideoRelation> =
        getVideosInPlayListFlowPagingSource(
            buildVideoRawQuery(
                wheres.appendOrCreateWith {
                    listOf(
                        playListIdWhere(playListId.toString()),
                        videoNotDeletedWhere(),
                    )
                },
                mediaSorts,
            ),
        )

    private fun buildPlayListRawQuery(
        wheres: MediaWheres?,
        sort: MediaSorts?,
    ): RoomRawQuery {
        val sql = """
            SELECT * FROM play_list_table
            JOIN play_list_with_media_cross_ref_table 
                ON play_list_id = play_list_with_media_cross_ref_play_list_id
            LEFT JOIN library_media_table 
                ON play_list_with_media_cross_ref_media_store_id = media_id
            ${wheres.toWhereString()}
            ${sort.toSortString()}
        """
        return RoomRawQuery(sql)
    }

    private fun buildVideoRawQuery(
        wheres: MediaWheres?,
        sort: MediaSorts?,
    ): RoomRawQuery {
        val sql = """
            SELECT * FROM play_list_table
            JOIN play_list_with_media_cross_ref_table 
                ON play_list_id = play_list_with_media_cross_ref_play_list_id
            LEFT JOIN library_video_table 
                ON play_list_with_media_cross_ref_media_store_id = video_id
            ${wheres.toWhereString()}
            ${sort.toSortString()}
        """
        return RoomRawQuery(sql)
    }

    private fun audioNotDeletedWhere() =
        Where(
            "deleted",
            "IS NOT",
            "1",
        )

    private fun videoNotDeletedWhere() =
        Where(
            "video_deleted",
            "IS NOT",
            "1",
        )

    private fun playListIdWhere(playListId: String) =
        Where(
            "play_list_id",
            "=",
            playListId,
        )

    @RawQuery(observedEntities = [MediaEntity::class, PlayListEntity::class, PlayListWithMediaCrossRef::class])
    fun getMediasInPlayListFlowRaw(rawQuery: RoomRawQuery): Flow<List<CrossRefWithMediaRelation>>

    @RawQuery(observedEntities = [VideoEntity::class, PlayListEntity::class, PlayListWithMediaCrossRef::class])
    fun getVideosInPlayListFlowRaw(rawQuery: RoomRawQuery): Flow<List<CrossRefWithVideoRelation>>

    @RawQuery(observedEntities = [MediaEntity::class, PlayListEntity::class, PlayListWithMediaCrossRef::class])
    fun getMediasInPlayListFlowPagingSource(rawQuery: RoomRawQuery): PagingSource<Int, CrossRefWithMediaRelation>

    @RawQuery(observedEntities = [VideoEntity::class, PlayListEntity::class, PlayListWithMediaCrossRef::class])
    fun getVideosInPlayListFlowPagingSource(rawQuery: RoomRawQuery): PagingSource<Int, CrossRefWithVideoRelation>
}
