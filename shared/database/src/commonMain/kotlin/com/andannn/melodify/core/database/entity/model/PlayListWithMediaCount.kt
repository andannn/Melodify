/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.entity.model

import androidx.room.Embedded
import com.andannn.melodify.core.database.entity.PlayListEntity

data class PlayListWithMediaCount(
    @Embedded
    val playListEntity: PlayListEntity,
    val mediaCount: Int,
)
