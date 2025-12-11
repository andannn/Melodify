package com.andannn.melodify.util.orientation

interface ScreenOrientationController {
    val isCurrentPortrait: Boolean

    fun requestLandscape()

    fun isRequestLandscape(): Boolean

    fun cancelRequest()
}
