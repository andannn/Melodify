/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.internal

import androidx.media3.common.Player
import com.andannn.melodify.core.data.model.PlayMode

fun fromRepeatMode(
    @Player.RepeatMode repeatMode: Int,
) = when (repeatMode) {
    Player.REPEAT_MODE_ONE -> PlayMode.REPEAT_ONE
    Player.REPEAT_MODE_OFF -> PlayMode.REPEAT_OFF
    Player.REPEAT_MODE_ALL -> PlayMode.REPEAT_ALL
    else -> error("Invalid param $repeatMode")
}

fun PlayMode.toExoPlayerMode() =
    when (this) {
        PlayMode.REPEAT_ONE -> Player.REPEAT_MODE_ONE
        PlayMode.REPEAT_OFF -> Player.REPEAT_MODE_OFF
        PlayMode.REPEAT_ALL -> Player.REPEAT_MODE_ALL
    }
