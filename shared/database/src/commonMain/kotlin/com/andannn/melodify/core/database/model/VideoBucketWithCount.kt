package com.andannn.melodify.core.database.model

import androidx.room3.ColumnInfo

data class VideoBucketWithCount(
    @ColumnInfo(name = "video_bucket_id")
    val bucketId: Long,
    @ColumnInfo(name = "video_bucket_display_name")
    val bucketDisplayName: String? = null,
    @ColumnInfo(name = "track_count")
    val count : Int
)