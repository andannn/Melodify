package com.andannn.melodify.util.orientation

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration

@Suppress("ktlint:standard:function-naming")
fun ScreenOrientationController(activity: Activity): ScreenOrientationController = ScreenOrientationControllerImpl(activity)

private class ScreenOrientationControllerImpl(
    private val activity: Activity,
) : ScreenOrientationController {
    override val isCurrentPortrait: Boolean
        get() = activity.orientation == Configuration.ORIENTATION_PORTRAIT

    override fun requestLandscape() {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    override fun isRequestLandscape(): Boolean = activity.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

    override fun cancelRequest() {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }
}

private val Activity.orientation
    get() = resources?.configuration?.orientation ?: Configuration.ORIENTATION_UNDEFINED
