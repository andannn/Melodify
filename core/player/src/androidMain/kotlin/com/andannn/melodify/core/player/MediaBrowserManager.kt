package com.andannn.melodify.core.player

import android.app.Application
import android.content.ComponentName
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.withTimeout
import timber.log.Timber
import kotlin.jvm.Throws

private const val TAG = "MediaBrowserManager"

interface MediaBrowserManager {
    val mediaBrowser: MediaBrowser

    @Throws(TimeoutCancellationException::class)
    suspend fun connect()

    fun disConnect()
}

class MediaBrowserManagerImpl(
    private val application: Application,
) : MediaBrowserManager {
    private var _mediaBrowser: MediaBrowser? = null

    override val mediaBrowser: MediaBrowser
        get() {
            return _mediaBrowser ?: throw IllegalStateException("MediaBrowser is not initialized")
        }

    override suspend fun connect() {
        Timber.tag(TAG).d("connect: start")
        _mediaBrowser = withTimeout(5000) {
            providerMediaBrowser(application).await()
        }
        Timber.tag(TAG).d("connect: finish")
    }

    override fun disConnect() {
        _mediaBrowser?.release()
        _mediaBrowser = null
    }
}

private fun providerMediaBrowser(application: Application): ListenableFuture<MediaBrowser> {
    return MediaBrowser.Builder(
        application,
        SessionToken(
            application,
            ComponentName(application, PlayerService::class.java.name),
        ),
    )
        .buildAsync()
}