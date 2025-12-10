package com.andannn.melodify

import android.app.Activity
import android.app.PictureInPictureParams
import android.os.Build
import android.util.Rational
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.app.PictureInPictureModeChangedInfo
import androidx.core.util.Consumer

class PipParamUpdater(
    val activity: Activity,
) {
    var isAutoEnterEnabled: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                updatePictureInPictureParams()
            }
        }

    var aspectRatio: Rational = Rational(16, 9)
        set(value) {
            if (field != value) {
                field = value
                updatePictureInPictureParams()
            }
        }

    init {
        updatePictureInPictureParams()
    }

    private fun updatePictureInPictureParams() {
        activity.setPictureInPictureParams(
            PictureInPictureParams
                .Builder()
                .apply {
                    setAspectRatio(aspectRatio)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        setAutoEnterEnabled(isAutoEnterEnabled)
                    }
                }
//                .setSourceRectHint(sourceRectHint)
                .build(),
        )
    }
}

@Composable
fun rememberIsInPipMode(): Boolean {
    val activity = LocalActivity.current as? androidx.activity.ComponentActivity ?: return false
    var pipMode by remember { mutableStateOf(activity.isInPictureInPictureMode) }
    DisposableEffect(activity) {
        val observer =
            Consumer<PictureInPictureModeChangedInfo> { info ->
                pipMode = info.isInPictureInPictureMode
            }
        activity.addOnPictureInPictureModeChangedListener(
            observer,
        )
        onDispose { activity.removeOnPictureInPictureModeChangedListener(observer) }
    }
    return pipMode
}
