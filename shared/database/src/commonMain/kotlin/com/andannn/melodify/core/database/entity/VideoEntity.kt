/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

object VideoColumns {
    const val ID = "video_id"
    const val SOURCE_URI = "video_source_uri"
    const val TITLE = "video_title"
    const val BUCKET_ID = "video_bucket_id"
    const val BUCKET_DISPLAY_NAME = "video_bucket_display_name"

    const val ALBUM = "video_album"

    const val DELETED = "video_deleted"
}

@Entity(
    tableName = "library_video_table",
)
data class VideoEntity constructor(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "video_id")
    val id: Long = 0,
    @ColumnInfo(name = "video_file_path")
    val path: String? = null,
    @ColumnInfo(name = "video_source_uri")
    val sourceUri: String? = null,
    @ColumnInfo(name = "video_title")
    val title: String? = null,
    @ColumnInfo(name = "video_duration")
    val duration: Int? = null,
    @ColumnInfo(name = "video_modified_date")
    val modifiedDate: Long? = null,
    @ColumnInfo(name = "video_size")
    val size: Int? = null,
    @ColumnInfo(name = "video_mime_type")
    val mimeType: String? = null,
    // video-specific
    @ColumnInfo(name = "video_width")
    val width: Int? = null,
    @ColumnInfo(name = "video_height")
    val height: Int? = null,
    @ColumnInfo(name = "video_orientation")
    val orientation: Int? = null,
    @ColumnInfo(name = "video_resolution")
    val resolution: String? = null,
    @ColumnInfo(name = "video_relative_path")
    val relativePath: String? = null,
    @ColumnInfo(name = "video_bucket_id")
    val bucketId: Long? = null,
    @ColumnInfo(name = "video_bucket_display_name")
    val bucketDisplayName: String? = null,
    @ColumnInfo(name = "video_volume_name")
    val volumeName: String? = null,
    @ColumnInfo(name = "video_album")
    val album: String? = null,
    @ColumnInfo(name = "video_artist")
    val artist: String? = null,
    @ColumnInfo(name = "video_date_added")
    val dateAdded: Long? = null,
    @ColumnInfo(name = "video_date_modified")
    val dateModified: Long? = null,
    @ColumnInfo(name = "video_deleted")
    val deleted: Int? = null,
)
