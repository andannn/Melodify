/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class PlayListAndMedias(
    @Embedded
    val playList: PlayListEntity,
    @Relation(
        parentColumn = PlayListColumns.ID,
        entityColumn = PlayListWithMediaCrossRefColumns.PLAY_LIST_ID,
    )
    val medias: List<PlayListWithMediaCrossRef>,
)
