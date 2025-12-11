/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.util

import android.app.Activity
import android.app.PictureInPictureParams
import android.os.Build
import android.util.Rational
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.core.app.PictureInPictureModeChangedInfo
import androidx.core.util.Consumer
import com.andannn.melodify.core.data.PlayerStateMonitoryRepository
import com.andannn.melodify.core.data.model.VideoItemModel
import com.andannn.melodify.ui.core.LocalRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay

private const val TAG = "PipParamUpdater"

@Composable
fun PipParamUpdateEffect(playerStateMonitoryRepository: PlayerStateMonitoryRepository = LocalRepository.current) {
    val activity = LocalActivity.current ?: return
    val pipParamUpdater = remember { PipParamUpdater(activity) }
    val isPlaying by playerStateMonitoryRepository.observeIsPlaying().collectAsState(false)
    val playingItem by playerStateMonitoryRepository.getPlayingMediaStateFlow().collectAsState(null)
    val videoSize =
        remember(playingItem) {
            when (val item = playingItem) {
                is VideoItemModel -> {
                    Size(item.width.toFloat(), item.height.toFloat())
                }

                else -> {
                    null
                }
            }
        }
    LaunchedEffect(pipParamUpdater, videoSize, isPlaying) {
        delay(50)
        pipParamUpdater.isAutoEnterEnabled = isPlaying
        if (videoSize != null) {
            pipParamUpdater.aspectRatio =
                Rational(videoSize.width.toInt(), videoSize.height.toInt())
        }
    }
}

@Composable
fun rememberIsInPipMode(): Boolean {
    val activity = LocalActivity.current as? ComponentActivity ?: return false
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

private class PipParamUpdater(
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
        Napier.d(tag = TAG) { "updatePictureInPictureParams() aspectRatio: $aspectRatio isAutoEnterEnabled: $isAutoEnterEnabled" }
        activity.setPictureInPictureParams(
            PictureInPictureParams
                .Builder()
                .apply {
                    if (aspectRatio.toFloat() in 0.418410..2.390000) {
                        setAspectRatio(aspectRatio)
                    } else {
                        Napier.w(tag = TAG) { "aspectRatio: $aspectRatio is not supported" }
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        setAutoEnterEnabled(isAutoEnterEnabled)
                    }
                }.build(),
        )
    }
}
