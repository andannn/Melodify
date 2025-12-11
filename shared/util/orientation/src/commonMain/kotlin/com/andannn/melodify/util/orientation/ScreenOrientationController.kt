/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.util.orientation

interface ScreenOrientationController {
    val isCurrentPortrait: Boolean

    fun requestLandscape()

    fun isRequestLandscape(): Boolean

    fun cancelRequest()
}
