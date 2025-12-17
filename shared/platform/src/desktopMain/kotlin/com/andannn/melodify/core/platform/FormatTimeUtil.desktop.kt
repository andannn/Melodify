/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.platform

actual fun formatTime(
    minutes: Long,
    seconds: Int,
): String = "%02d:%02d".format(minutes, seconds)
