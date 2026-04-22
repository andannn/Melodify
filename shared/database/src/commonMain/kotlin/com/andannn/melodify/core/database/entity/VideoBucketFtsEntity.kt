package com.andannn.melodify.core.database.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Fts4
import androidx.room3.PrimaryKey

@Fts4(
    contentEntity = VideoBucketEntity::class,
)
@Entity(tableName = "library_video_bucket_fts_table")
class VideoBucketFtsEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    val rowId: Int,
    @ColumnInfo(name = "video_bucket_display_name")
    val bucketDisplayName: String? = null,
)