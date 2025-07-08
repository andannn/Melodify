package com.andannn.melodify.core.database.entity

import androidx.room.Embedded

data class CrossRefWithMediaRelation(
    @Embedded val playListWithMediaCrossRef: PlayListWithMediaCrossRef,
    @Embedded val media: MediaEntity,
)
