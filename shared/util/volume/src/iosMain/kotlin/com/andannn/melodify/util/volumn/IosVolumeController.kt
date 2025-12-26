/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.util.volumn

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class IosVolumeController : VolumeController {
    override fun getCurrentVolume(): Int = 0

    override fun getCurrentVolumeFlow(): Flow<Int> = flowOf(0)

    override fun getMainVolumeIndex(): Int = 0

    override fun setVolume(volumeIndex: Int) {
    }
}
