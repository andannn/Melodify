/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.player

import android.app.Application
import android.content.ComponentName
import androidx.media3.common.Player
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import io.github.aakira.napier.Napier
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.withTimeout
import kotlin.jvm.Throws

private const val TAG = "MediaBrowserManager"

interface MediaBrowserManager {
    val mediaBrowser: Player

    @Throws(TimeoutCancellationException::class)
    suspend fun connect()

    fun disConnect()
}

class DummyMediaBrowserManager : MediaBrowserManager {
    override val mediaBrowser: Player
        get() = throw IllegalStateException("MediaBrowser is not initialized")

    override suspend fun connect() = Unit

    override fun disConnect() = Unit
}

class MediaBrowserManagerImpl(
    private val application: Application,
) : MediaBrowserManager {
    private var _mediaBrowser: MediaBrowser? = null

    override val mediaBrowser: Player
        get() {
            return _mediaBrowser ?: throw IllegalStateException("MediaBrowser is not initialized")
        }

    override suspend fun connect() {
        Napier.d(tag = TAG) { "connect: start" }
        _mediaBrowser =
            withTimeout(5000) {
                providerMediaBrowser(application).await()
            }
        Napier.d(tag = TAG) { "connect: finish" }
    }

    override fun disConnect() {
        _mediaBrowser?.release()
        _mediaBrowser = null
    }
}

private fun providerMediaBrowser(application: Application): ListenableFuture<MediaBrowser> =
    MediaBrowser
        .Builder(
            application,
            SessionToken(
                application,
                ComponentName(application, PlayerService::class.java.name),
            ),
        ).buildAsync()
