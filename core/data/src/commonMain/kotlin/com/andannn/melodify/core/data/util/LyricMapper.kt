/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.util

import com.andannn.melodify.core.data.model.LyricModel
import com.andannn.melodify.core.database.entity.LyricEntity
import com.andannn.melodify.core.network.model.LyricData

fun LyricEntity.toLyricModel(): LyricModel =
    LyricModel(
        plainLyrics = plainLyrics,
        syncedLyrics = syncedLyrics,
    )

fun LyricData.toLyricEntity() =
    LyricEntity(
        id = id,
        name = name,
        trackName = trackName,
        artistName = artistName,
        albumName = albumName,
        duration = duration,
        instrumental = instrumental,
        plainLyrics = plainLyrics,
        syncedLyrics = syncedLyrics,
    )
