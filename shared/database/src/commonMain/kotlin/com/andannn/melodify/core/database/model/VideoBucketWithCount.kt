package com.andannn.melodify.core.database.model

import androidx.room3.ColumnInfo
import androidx.room3.Embedded
import com.andannn.melodify.core.database.entity.VideoBucketEntity

data class VideoBucketWithCount(
    @Embedded
    val videoBucket: VideoBucketEntity,
    @ColumnInfo(name = "track_count")
    val count : Int
)