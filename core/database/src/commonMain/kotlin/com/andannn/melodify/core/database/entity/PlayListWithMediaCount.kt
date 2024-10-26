package com.andannn.melodify.core.database.entity

import androidx.room.Embedded

data class PlayListWithMediaCount(
    @Embedded
    val playListEntity: PlayListEntity,
    val mediaCount: Int
)