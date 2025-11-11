/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andannn.melodify.core.database.Tables

object VideoColumns {
    const val ID = "video_id"
    const val FILE_PATH = "video_file_path"
    const val SOURCE_URI = "video_source_uri"
    const val TITLE = "video_title"
    const val DURATION = "video_duration"
    const val SIZE = "video_size"
    const val MIME_TYPE = "video_mime_type"

    const val WIDTH = "video_width"
    const val HEIGHT = "video_height"
    const val ORIENTATION = "video_orientation"
    const val RESOLUTION = "video_resolution"

    const val RELATIVE_PATH = "video_relative_path"
    const val BUCKET_ID = "video_bucket_id"
    const val BUCKET_DISPLAY_NAME = "video_bucket_display_name"
    const val VOLUME_NAME = "video_volume_name"

    const val ALBUM = "video_album"
    const val ARTIST = "video_artist"

    const val DATE_ADDED = "video_date_added"
    const val DATE_MODIFIED = "video_date_modified"
    const val MODIFIED_DATE = "video_modified_date"

    const val DELETED = "video_deleted"
}

@Entity(
    tableName = Tables.LIBRARY_VIDEO,
)
data class VideoEntity constructor(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = VideoColumns.ID)
    val id: Long = 0,
    @ColumnInfo(name = VideoColumns.FILE_PATH)
    val path: String? = null,
    @ColumnInfo(name = VideoColumns.SOURCE_URI)
    val sourceUri: String? = null,
    @ColumnInfo(name = VideoColumns.TITLE)
    val title: String? = null,
    @ColumnInfo(name = VideoColumns.DURATION)
    val duration: Int? = null,
    @ColumnInfo(name = VideoColumns.MODIFIED_DATE)
    val modifiedDate: Long? = null,
    @ColumnInfo(name = VideoColumns.SIZE)
    val size: Int? = null,
    @ColumnInfo(name = VideoColumns.MIME_TYPE)
    val mimeType: String? = null,
    // video-specific
    @ColumnInfo(name = VideoColumns.WIDTH)
    val width: Int? = null,
    @ColumnInfo(name = VideoColumns.HEIGHT)
    val height: Int? = null,
    @ColumnInfo(name = VideoColumns.ORIENTATION)
    val orientation: Int? = null,
    @ColumnInfo(name = VideoColumns.RESOLUTION)
    val resolution: String? = null,
    @ColumnInfo(name = VideoColumns.RELATIVE_PATH)
    val relativePath: String? = null,
    @ColumnInfo(name = VideoColumns.BUCKET_ID)
    val bucketId: Long? = null,
    @ColumnInfo(name = VideoColumns.BUCKET_DISPLAY_NAME)
    val bucketDisplayName: String? = null,
    @ColumnInfo(name = VideoColumns.VOLUME_NAME)
    val volumeName: String? = null,
    @ColumnInfo(name = VideoColumns.ALBUM)
    val album: String? = null,
    @ColumnInfo(name = VideoColumns.ARTIST)
    val artist: String? = null,
    @ColumnInfo(name = VideoColumns.DATE_ADDED)
    val dateAdded: Long? = null,
    @ColumnInfo(name = VideoColumns.DATE_MODIFIED)
    val dateModified: Long? = null,
    @ColumnInfo(name = VideoColumns.DELETED)
    val deleted: Int? = null,
)
