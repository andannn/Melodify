/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.platform

actual fun formatTime(
    minutes: Long,
    seconds: Int,
): String {
    val formattedMinutes = minutes.toString().padStart(2, '0')
    val formattedSeconds = seconds.toString().padStart(2, '0')
    return "$formattedMinutes:$formattedSeconds"
}
