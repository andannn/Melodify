/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.player.internal.util

import com.andannn.melodify.core.platform.formatTime
import kotlin.time.Duration.Companion.milliseconds

fun formatDuration(millis: Long): String {
    val d = millis.milliseconds
    val minutes = d.inWholeMinutes
    val seconds = d.inWholeSeconds % 60
    return formatTime(minutes, seconds.toInt())
}
