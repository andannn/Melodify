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
import androidx.room.Transaction
import com.andannn.melodify.core.database.MediaSorts
import com.andannn.melodify.core.database.MediaWheres
import com.andannn.melodify.core.database.Where
import com.andannn.melodify.core.database.appendOrCreateWith
import com.andannn.melodify.core.database.entity.MediaColumns
import com.andannn.melodify.core.database.entity.MediaEntity
import com.andannn.melodify.core.database.entity.model.PlayListAndMedias
import com.andannn.melodify.core.database.entity.PlayListColumns
import com.andannn.melodify.core.database.entity.PlayListEntity
import com.andannn.melodify.core.database.entity.PlayListWithMediaCrossRef
import com.andannn.melodify.core.database.entity.PlayListWithMediaCrossRefColumns
import com.andannn.melodify.core.database.entity.VideoColumns
import com.andannn.melodify.core.database.entity.VideoEntity
import com.andannn.melodify.core.database.entity.model.CrossRefWithMediaRelation
import com.andannn.melodify.core.database.entity.model.CrossRefWithVideoRelation
import com.andannn.melodify.core.database.entity.model.PlayListWithMediaCount
import com.andannn.melodify.core.database.toSortString
import com.andannn.melodify.core.database.toWhereString
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayListDao {
    @Query(
        """
        select play_list_table.*, COUNT(${PlayListWithMediaCrossRefColumns.MEDIA_STORE_ID}) as mediaCount
        from play_list_table
        left join play_list_with_media_cross_ref_table
            on ${PlayListColumns.ID} = ${PlayListWithMediaCrossRefColumns.PLAY_LIST_ID}
        where ${PlayListColumns.IS_AUDIO_PLAYLIST} = :isAudio
        group by ${PlayListColumns.ID}
        order by ${PlayListColumns.CREATED_DATE} desc
    """,
    )
    fun getAllPlayListFlow(isAudio: Boolean): Flow<List<PlayListWithMediaCount>>

    @Query(
        """
        select play_list_table.*, COUNT(${PlayListWithMediaCrossRefColumns.MEDIA_STORE_ID}) as mediaCount
        from play_list_table
        left join play_list_with_media_cross_ref_table
            on ${PlayListColumns.ID} = ${PlayListWithMediaCrossRefColumns.PLAY_LIST_ID}
        group by ${PlayListColumns.ID}
        order by ${PlayListColumns.CREATED_DATE} desc
    """,
    )
    fun getAllPlayListFlow(): Flow<List<PlayListWithMediaCount>>

    @Insert(entity = PlayListEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlayListEntities(entities: List<PlayListEntity>): List<Long>

    @Insert(entity = PlayListWithMediaCrossRef::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlayListWithMediaCrossRef(crossRefs: List<PlayListWithMediaCrossRef>): List<Long>

    @Query(
        """
        select ${PlayListWithMediaCrossRefColumns.MEDIA_STORE_ID}
        from play_list_with_media_cross_ref_table
        where ${PlayListWithMediaCrossRefColumns.PLAY_LIST_ID} = :playListId and
            ${PlayListWithMediaCrossRefColumns.MEDIA_STORE_ID} in (:mediaIdList)
    """,
    )
    suspend fun getDuplicateMediaInPlayList(
        playListId: Long,
        mediaIdList: List<String>,
    ): List<String>

    @Query(
        """
            delete from play_list_with_media_cross_ref_table
            where ${PlayListWithMediaCrossRefColumns.PLAY_LIST_ID} = :playListId and
                ${PlayListWithMediaCrossRefColumns.MEDIA_STORE_ID} in (:mediaIdList)
    """,
    )
    suspend fun deleteMediaFromPlayList(
        playListId: Long,
        mediaIdList: List<String>,
    )

    @Query(
        """
        select * from play_list_table
        where ${PlayListColumns.ID} = :playListId
    """,
    )
    @Transaction
    fun getPlayListFlowById(playListId: Long): Flow<PlayListAndMedias?>

    @Query(
        """
        select * from play_list_table
        where ${PlayListColumns.ID} = :playListId
    """,
    )
    @Transaction
    suspend fun getPlayListWithMedias(playListId: Long): PlayListAndMedias?

    @Query(
        """
        select * from play_list_table
        where ${PlayListColumns.ID} = :playListId
    """,
    )
    suspend fun getPlayListEntity(playListId: Long): PlayListEntity?

    @Query(
        """
        select * from play_list_table
        where ${PlayListColumns.ID} = :playListId
    """,
    )
    @Transaction
    fun getPlayListFlow(playListId: Long): Flow<PlayListAndMedias?>

    @Query(
        """
        select exists(
            select 1 from play_list_with_media_cross_ref_table
            where ${PlayListWithMediaCrossRefColumns.PLAY_LIST_ID} = :playList and
                ${PlayListWithMediaCrossRefColumns.MEDIA_STORE_ID} = :mediaStoreId
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
        WHERE ${PlayListColumns.IS_FAVORITE_PLAYLIST} = 1 AND ${PlayListColumns.IS_AUDIO_PLAYLIST} = 1
    """,
    )
    fun getFavoriteAudioPlayListFlow(): Flow<PlayListEntity?>

    @Query(
        """
        SELECT * FROM play_list_table
        WHERE ${PlayListColumns.IS_FAVORITE_PLAYLIST} = 1 AND ${PlayListColumns.IS_AUDIO_PLAYLIST} = 0
    """,
    )
    fun getFavoriteVideoPlayListFlow(): Flow<PlayListEntity?>

    @Query(
        """
        delete from play_list_table
        where ${PlayListColumns.ID} = :playListId
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
            JOIN play_list_with_media_cross_ref_table ON ${PlayListColumns.ID} = ${PlayListWithMediaCrossRefColumns.PLAY_LIST_ID}
            LEFT JOIN library_media_table ON ${PlayListWithMediaCrossRefColumns.MEDIA_STORE_ID} = ${MediaColumns.ID}
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
            JOIN play_list_with_media_cross_ref_table ON ${PlayListColumns.ID} = ${PlayListWithMediaCrossRefColumns.PLAY_LIST_ID}
            LEFT JOIN library_video_table ON ${PlayListWithMediaCrossRefColumns.MEDIA_STORE_ID} = ${VideoColumns.ID}
            ${wheres.toWhereString()}
            ${sort.toSortString()}
        """
        return RoomRawQuery(sql)
    }

    private fun audioNotDeletedWhere() =
        Where(
            MediaColumns.DELETED,
            "IS NOT",
            "1",
        )

    private fun videoNotDeletedWhere() =
        Where(
            VideoColumns.DELETED,
            "IS NOT",
            "1",
        )

    private fun playListIdWhere(playListId: String) =
        Where(
            PlayListColumns.ID,
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
