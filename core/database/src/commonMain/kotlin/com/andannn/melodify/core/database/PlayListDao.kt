package com.andannn.melodify.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.andannn.melodify.core.database.entity.PlayListAndMedias
import com.andannn.melodify.core.database.entity.PlayListColumns
import com.andannn.melodify.core.database.entity.PlayListEntity
import com.andannn.melodify.core.database.entity.PlayListWithMediaCount
import com.andannn.melodify.core.database.entity.PlayListWithMediaCrossRef
import com.andannn.melodify.core.database.entity.PlayListWithMediaCrossRefColumns
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayListDao {
    @Query("""
        select ${PlayListColumns.ID}, ${PlayListColumns.CREATED_DATE}, ${PlayListColumns.NAME}, ${PlayListColumns.ARTWORK_URI}, COUNT(${PlayListWithMediaCrossRefColumns.MEDIA_STORE_ID}) as mediaCount
        from ${Tables.PLAY_LIST}
        left join ${Tables.PLAY_LIST_WITH_MEDIA_CROSS_REF}
            on ${PlayListColumns.ID} = ${PlayListWithMediaCrossRefColumns.PLAY_LIST_ID}
        group by ${PlayListColumns.ID}
        order by ${PlayListColumns.CREATED_DATE} desc
    """)
    fun getPlayListFlow(): Flow<List<PlayListWithMediaCount>>

    @Insert(entity = PlayListEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlayListEntities(entities: List<PlayListEntity>)

    @Insert(entity = PlayListWithMediaCrossRef::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlayListWithMediaCrossRef(crossRefs: List<PlayListWithMediaCrossRef>): List<Long>

    @Query("""
        select * from ${Tables.PLAY_LIST}
        where ${PlayListColumns.ID} = :playListId
    """)
    fun getPlayListFlow(playListId: Long): Flow<PlayListAndMedias>

    @Query("""
        select * from ${Tables.PLAY_LIST}
        where ${PlayListColumns.ID} = :playListId
    """)
    fun getPlayList(playListId: Long): PlayListAndMedias?
}