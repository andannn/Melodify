/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andannn.melodify.core.database.Tables

internal object LyricColumns {
    const val ID = "lyric_id"
    const val NAME = "lyric_name"
    const val TRACK_NAME = "lyric_track_name"
    const val ARTIST_NAME = "lyric_artist_name"
    const val ALBUM_NAME = "lyric_album_name"
    const val DURATION = "lyric_duration_name"
    const val INSTRUMENTAL = "lyric_instrumental"
    const val PLAIN_LYRICS = "lyric_plain_lyrics"
    const val SYNCED_LYRICS = "lyric_synced_lyrics"
}

@Entity(tableName = Tables.LYRIC)
data class LyricEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = LyricColumns.ID)
    val id: Long,
    @ColumnInfo(name = LyricColumns.NAME)
    val name: String,
    @ColumnInfo(name = LyricColumns.TRACK_NAME)
    val trackName: String,
    @ColumnInfo(name = LyricColumns.ARTIST_NAME)
    val artistName: String,
    @ColumnInfo(name = LyricColumns.ALBUM_NAME)
    val albumName: String,
    @ColumnInfo(name = LyricColumns.DURATION)
    val duration: Double,
    @ColumnInfo(name = LyricColumns.INSTRUMENTAL)
    val instrumental: Boolean,
    @ColumnInfo(name = LyricColumns.PLAIN_LYRICS)
    val plainLyrics: String,
    @ColumnInfo(name = LyricColumns.SYNCED_LYRICS)
    val syncedLyrics: String,
)
