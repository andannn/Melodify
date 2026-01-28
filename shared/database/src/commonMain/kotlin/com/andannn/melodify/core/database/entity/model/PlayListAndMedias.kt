/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.entity.model

import androidx.room.Embedded
import androidx.room.Relation
import com.andannn.melodify.core.database.entity.PlayListEntity
import com.andannn.melodify.core.database.entity.PlayListWithMediaCrossRef

data class PlayListAndMedias(
    @Embedded
    val playList: PlayListEntity,
    @Relation(
        parentColumn = "play_list_id",
        entityColumn = "play_list_with_media_cross_ref_play_list_id",
    )
    val medias: List<PlayListWithMediaCrossRef>,
)
