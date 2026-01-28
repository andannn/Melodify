package com.andannn.melodify.core.database.entity.model

import androidx.room.Embedded
import androidx.room.Relation
import com.andannn.melodify.core.database.entity.PlayListColumns
import com.andannn.melodify.core.database.entity.PlayListEntity
import com.andannn.melodify.core.database.entity.PlayListWithMediaCrossRef
import com.andannn.melodify.core.database.entity.PlayListWithMediaCrossRefColumns

data class PlayListAndMedias(
    @Embedded
    val playList: PlayListEntity,
    @Relation(
        parentColumn = PlayListColumns.ID,
        entityColumn = PlayListWithMediaCrossRefColumns.PLAY_LIST_ID,
    )
    val medias: List<PlayListWithMediaCrossRef>,
)
