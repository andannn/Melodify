/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.RoomRawQuery
import androidx.room.Transaction
import com.andannn.melodify.core.database.Tables
import com.andannn.melodify.core.database.entity.CrossRefWithMediaRelation
import com.andannn.melodify.core.database.entity.MediaColumns
import com.andannn.melodify.core.database.entity.MediaEntity
import com.andannn.melodify.core.database.entity.PlayListAndMedias
import com.andannn.melodify.core.database.entity.PlayListColumns
import com.andannn.melodify.core.database.entity.PlayListEntity
import com.andannn.melodify.core.database.entity.PlayListWithMediaCount
import com.andannn.melodify.core.database.entity.PlayListWithMediaCrossRef
import com.andannn.melodify.core.database.entity.PlayListWithMediaCrossRefColumns
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow

private const val TAG = "PlayListDao"

@Dao
interface PlayListDao {
    companion object {
        const val FAVORITE_PLAY_LIST_ID = 0L
    }

    @Query(
        """
        select ${PlayListColumns.ID}, ${PlayListColumns.CREATED_DATE}, ${PlayListColumns.NAME}, ${PlayListColumns.ARTWORK_URI}, COUNT(${PlayListWithMediaCrossRefColumns.MEDIA_STORE_ID}) as mediaCount
        from ${Tables.PLAY_LIST}
        left join ${Tables.PLAY_LIST_WITH_MEDIA_CROSS_REF}
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
        from ${Tables.PLAY_LIST_WITH_MEDIA_CROSS_REF}
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
            delete from ${Tables.PLAY_LIST_WITH_MEDIA_CROSS_REF}
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
        select * from ${Tables.PLAY_LIST}
        where ${PlayListColumns.ID} = :playListId
    """,
    )
    @Transaction
    fun getPlayListFlowById(playListId: Long): Flow<PlayListAndMedias?>

    @Query(
        """
        select * from ${Tables.PLAY_LIST}
        where ${PlayListColumns.ID} = :playListId
    """,
    )
    @Transaction
    suspend fun getPlayList(playListId: Long): PlayListAndMedias?

    @Query(
        """
        select * from ${Tables.PLAY_LIST}
        where ${PlayListColumns.ID} = :playListId
    """,
    )
    @Transaction
    fun getPlayListFlow(playListId: Long): Flow<PlayListAndMedias?>

    @Query(
        """
        select exists(
            select 1 from ${Tables.PLAY_LIST_WITH_MEDIA_CROSS_REF}
            where ${PlayListWithMediaCrossRefColumns.PLAY_LIST_ID} = :playList and
                ${PlayListWithMediaCrossRefColumns.MEDIA_STORE_ID} = :mediaStoreId
        )
    """,
    )
    fun getIsMediaInPlayListFlow(
        playList: String,
        mediaStoreId: String,
    ): Flow<Boolean>

    @Insert(entity = PlayListEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun inertPlayLists(entities: List<PlayListEntity>): List<Long>

    @Query(
        """
        delete from ${Tables.PLAY_LIST}
        where ${PlayListColumns.ID} = :playListId
    """,
    )
    suspend fun deletePlayListById(playListId: Long)

    fun getMediasInPlayListFlow(
        playListId: Long,
        sortMethod: SortMethod? = null,
    ): Flow<List<CrossRefWithMediaRelation>> =
        getMediasInPlayListFlowRaw(
            buildPlayListRawQuery(playListId, sortMethod),
        )

    private fun buildPlayListRawQuery(
        playListId: Long,
        sort: SortMethod?,
    ): RoomRawQuery {
        val sort = sort?.toSortString() ?: ""
        val sql = """
            select * from ${Tables.PLAY_LIST}
            join ${Tables.PLAY_LIST_WITH_MEDIA_CROSS_REF} on ${PlayListColumns.ID} = ${PlayListWithMediaCrossRefColumns.PLAY_LIST_ID}
            left join ${Tables.LIBRARY_MEDIA} on ${PlayListWithMediaCrossRefColumns.MEDIA_STORE_ID} = ${MediaColumns.ID}
            where ${PlayListColumns.ID} = $playListId
            $sort
        """
        Napier.d(tag = TAG) { "buildPlayListRawQuery: $sql" }
        return RoomRawQuery(sql)
    }

    @RawQuery(observedEntities = [MediaEntity::class, PlayListEntity::class, PlayListWithMediaCrossRef::class])
    fun getMediasInPlayListFlowRaw(rawQuery: RoomRawQuery): Flow<List<CrossRefWithMediaRelation>>

    @Query(
        """
            select * from ${Tables.PLAY_LIST}
            join ${Tables.PLAY_LIST_WITH_MEDIA_CROSS_REF} on ${PlayListColumns.ID} = ${PlayListWithMediaCrossRefColumns.PLAY_LIST_ID}
            left join ${Tables.LIBRARY_MEDIA} on ${PlayListWithMediaCrossRefColumns.MEDIA_STORE_ID} = ${MediaColumns.ID}
            where ${PlayListColumns.ID} = :playListId
            order by ${MediaColumns.MODIFIED_DATE} desc
        """,
    )
    suspend fun getMediasInPlayList(playListId: Long): List<CrossRefWithMediaRelation>
}
