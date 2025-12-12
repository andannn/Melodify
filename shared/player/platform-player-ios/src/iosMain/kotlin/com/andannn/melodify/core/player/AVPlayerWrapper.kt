/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.player

interface AVPlayerWrapper {
    fun playUrl(url: String)

    fun pause()

    fun resume()

    fun stop()

    fun seekTo(positionMs: Long)

    val currentPositionMs: Long

    val currentDurationMs: Long

    var onCompleted: (() -> Unit)?

    var onProgress: ((positionMs: Long, durationMs: Long) -> Unit)?
}
