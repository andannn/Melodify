/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.player

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext

private const val TAG = "PlayerService"

class PlayerService :
    MediaSessionService(),
    CoroutineScope {
    private val playerWrapper: PlayerWrapper by inject()

    private val sleepCounterController: SleepTimerController by inject()

    private lateinit var session: MediaSession

    private val job = Job()

    override val coroutineContext: CoroutineContext = Dispatchers.Main + job

    companion object {
        private const val IMMUTABLE_FLAG = PendingIntent.FLAG_IMMUTABLE
    }

    override fun onCreate() {
        super.onCreate()

        val player =
            ExoPlayer
                .Builder(application)
                .setAudioAttributes(AudioAttributes.DEFAULT, true)
                .setHandleAudioBecomingNoisy(true)
                .build()

        playerWrapper.setUpPlayer(player)
        session =
            MediaSession
                .Builder(this, player)
                .setSessionActivity(getSingleTopActivity())
                .build()

        launch {
            sleepCounterController.getCounterStateFlow().collect {
                if (it is SleepTimeCounterState.Finish) {
                    Napier.d(tag = TAG) { "sleep counter finished" }
                    player.pause()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        playerWrapper.release()
        session.release()

        job.cancel()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = session

    private fun getSingleTopActivity(): PendingIntent =
        PendingIntent.getActivity(
            this,
            0,
            Intent(this, Class.forName("com.andannn.melodify.MainActivity")),
            IMMUTABLE_FLAG or PendingIntent.FLAG_UPDATE_CURRENT,
        )
}
