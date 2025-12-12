/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.syncer.model

data class AudioData(
    val id: Long,
    val path: String = "",
    val sourceUri: String,
    val title: String = "",
    val duration: Int? = -1,
    val modifiedDate: Long? = -1,
    val size: Int? = -1,
    val mimeType: String? = "",
    val album: String? = null,
    val albumId: Long? = null,
    val artist: String? = null,
    val artistId: Long? = null,
    val cdTrackNumber: Int? = null,
    val discNumber: Int? = null,
    val numTracks: Int? = null,
    val bitrate: Int? = null,
    val genre: String? = null,
    val genreId: Long? = null,
    val year: String? = null,
    val composer: String? = null,
    val cover: String? = null,
)
