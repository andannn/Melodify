package com.andannn.melodify.core.database.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(
    tableName = "library_video_bucket_table",
)
class VideoBucketEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "video_bucket_id")
    val bucketId: Long,
    @ColumnInfo(name = "video_bucket_display_name")
    val bucketDisplayName: String? = null,
)