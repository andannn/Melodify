/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "lyric_table",
    foreignKeys = [
        ForeignKey(
            entity = AudioEntity::class,
            parentColumns = ["media_id"],
            childColumns = ["media_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["media_id"]),
    ],
)
data class LyricEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "lyric_id")
    val id: Long,
    @ColumnInfo(name = "media_id", defaultValue = "0")
    val mediaId: Long = 0,
    @ColumnInfo(name = "lyric_name")
    val name: String,
    @ColumnInfo(name = "lyric_track_name")
    val trackName: String,
    @ColumnInfo(name = "lyric_artist_name")
    val artistName: String,
    @ColumnInfo(name = "lyric_album_name")
    val albumName: String,
    @ColumnInfo(name = "lyric_duration_name")
    val duration: Double,
    @ColumnInfo(name = "lyric_instrumental")
    val instrumental: Boolean,
    @ColumnInfo(name = "lyric_plain_lyrics")
    val plainLyrics: String,
    @ColumnInfo(name = "lyric_synced_lyrics")
    val syncedLyrics: String,
)
