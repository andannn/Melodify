/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.entity

import androidx.room.Embedded

data class CrossRefWithMediaRelation(
    @Embedded val playListWithMediaCrossRef: PlayListWithMediaCrossRef,
    @Embedded val media: MediaEntity,
)
