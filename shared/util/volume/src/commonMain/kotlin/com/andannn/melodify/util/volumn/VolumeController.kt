/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.util.volumn

import kotlinx.coroutines.flow.Flow

interface VolumeController {
    fun getCurrentVolume(): Int

    fun getCurrentVolumeFlow(): Flow<Int>

    fun getMaxVolume(): Int

    fun setVolume(volumeIndex: Int)
}

fun VolumeController.adjustVolume(isPositive: Boolean) {
    val current = getCurrentVolume()
    val max = getMaxVolume()

    val new = (current + (if (isPositive) 1 else -1)).coerceIn(0, max)
    if (new == current) return
    setVolume(new)
}
