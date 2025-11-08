/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.model

enum class PlayMode {
    REPEAT_ONE,
    REPEAT_OFF,
    REPEAT_ALL,
}

fun PlayMode.next(): PlayMode {
    val nextIndex = (this.ordinal + 1) % PlayMode.entries.size
    return PlayMode.entries[nextIndex]
}
