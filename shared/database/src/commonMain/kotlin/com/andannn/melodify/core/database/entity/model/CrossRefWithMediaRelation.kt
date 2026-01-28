/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.entity.model

import androidx.room.Embedded
import com.andannn.melodify.core.database.entity.MediaEntity
import com.andannn.melodify.core.database.entity.PlayListWithMediaCrossRef
import com.andannn.melodify.core.database.entity.VideoEntity

data class CrossRefWithMediaRelation(
    @Embedded val playListWithMediaCrossRef: PlayListWithMediaCrossRef,
    @Embedded val media: MediaEntity,
)

data class CrossRefWithVideoRelation(
    @Embedded val playListWithMediaCrossRef: PlayListWithMediaCrossRef,
    @Embedded val media: VideoEntity,
)
