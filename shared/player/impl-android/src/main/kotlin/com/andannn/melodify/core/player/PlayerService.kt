/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.player

import android.app.PendingIntent
import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.ResolvingDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.andannn.melodify.core.network.service.siren.MonsterSirenService
import com.andannn.melodify.player.SleepTimeCounterState
import com.andannn.melodify.player.SleepTimerController
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext

private const val TAG = "PlayerService"

class PlayerService :
    MediaSessionService(),
    CoroutineScope {
    private val playerWrapper: ExoPlayerWrapper by inject()

    private val sleepCounterController: SleepTimerController by inject()
    private val sirenService: MonsterSirenService by inject()

    private lateinit var session: MediaSession

    private val job = Job()

    override val coroutineContext: CoroutineContext = Dispatchers.Main + job

    companion object {
        private const val IMMUTABLE_FLAG = PendingIntent.FLAG_IMMUTABLE
        private const val DEFAULT_SEEK_INCREMENT_MS = 10_000L
        private const val DEFAULT_SEEK_BACK_INCREMENT_MS = 10_000L
    }

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        val upstreamDataSourceFactory: DataSource.Factory =
            DefaultDataSource.Factory(this)

        val resolvingDataSourceFactory =
            ResolvingDataSource.Factory(
                upstreamDataSourceFactory,
            ) { dataSpec ->
                val originalUrl = dataSpec.uri.toString()
                if (originalUrl.contains("monster-siren.hypergryph.com")) {
                    Napier.d(tag = TAG) { "resolve siren url: $originalUrl, currentThread: ${Thread.currentThread()}" }
                    val sourceUrl =
                        runBlocking {
                            val cid = originalUrl.substringAfterLast("/")
                            sirenService.getSourceUrlOfSong(cid).getOrNull()
                        } ?: return@Factory dataSpec

                    Napier.d(tag = TAG) { "setup source url $sourceUrl, currentThread: ${Thread.currentThread()}" }
                    return@Factory dataSpec
                        .buildUpon()
                        .setUri(sourceUrl)
                        .build()
                }

                dataSpec
            }

        val player =
            ExoPlayer
                .Builder(application)
                .setAudioAttributes(AudioAttributes.DEFAULT, true)
                .setHandleAudioBecomingNoisy(true)
                .setSeekForwardIncrementMs(DEFAULT_SEEK_INCREMENT_MS)
                .setSeekBackIncrementMs(DEFAULT_SEEK_BACK_INCREMENT_MS)
                .setMediaSourceFactory(
                    DefaultMediaSourceFactory(this)
                        .setDataSourceFactory(resolvingDataSourceFactory),
                ).build()

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
