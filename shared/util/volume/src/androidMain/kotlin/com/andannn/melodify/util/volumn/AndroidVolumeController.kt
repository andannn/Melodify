/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.util.volumn

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import io.github.aakira.napier.Napier
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AndroidVolumeController(
    private val context: Context,
) : VolumeController {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    override fun getMainVolumeIndex(): Int = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

    override fun getCurrentVolume(): Int = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

    override fun getCurrentVolumeFlow(): Flow<Int> =
        callbackFlow {
            val receiver =
                object : BroadcastReceiver() {
                    override fun onReceive(
                        context: Context,
                        intent: Intent?,
                    ) {
                        if (intent?.action == "android.media.VOLUME_CHANGED_ACTION") {
                            val streamType = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1)
                            if (streamType == AudioManager.STREAM_MUSIC) {
                                val currentVol = getCurrentVolume()
                                trySendBlocking(currentVol)
                                    .onFailure { throwable ->
                                        Napier.e { "Failed to send current volume: $throwable" }
                                    }
                            }
                        }
                    }
                }
            val filter = IntentFilter("android.media.VOLUME_CHANGED_ACTION")
            context.registerReceiver(receiver, filter)

            trySendBlocking(getCurrentVolume())

            awaitClose {
                context.unregisterReceiver(receiver)
            }
        }

    override fun setVolume(volumeIndex: Int) {
        val safeVolume = volumeIndex.coerceIn(0, getMainVolumeIndex())

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, safeVolume, 0)
    }
}
