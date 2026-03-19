/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.model

import androidx.room3.ColumnInfo
import androidx.room3.Embedded
import com.andannn.melodify.core.database.entity.ArtistEntity

data class ArtistWithMediaCount(
    @Embedded
    val entity: ArtistEntity,
    @ColumnInfo(name = "track_count")
    val trackCount: Int,
)
