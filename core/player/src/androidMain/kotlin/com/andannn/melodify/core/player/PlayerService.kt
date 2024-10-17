package com.andannn.melodify.core.player

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaLibraryService.LibraryParams
import androidx.media3.session.MediaLibraryService.MediaLibrarySession
import androidx.media3.session.MediaSession
import com.andannn.melodify.core.player.library.MediaLibrarySource
import com.andannn.melodify.core.player.library.MediaLibrarySourceImpl
import com.andannn.melodify.core.player.library.mediastore.MediaStoreSourceImpl
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.guava.future
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import org.koin.android.ext.android.inject

private const val TAG = "PlayerService"

class PlayerService : MediaLibraryService(), CoroutineScope {
    private val playerWrapper: PlayerWrapper by inject()

    private val sleepCounterController: SleepTimerController by inject()

    private lateinit var mediaLibrarySession: MediaLibrarySession

    private lateinit var librarySessionCallback: CustomMediaLibrarySessionCallback

    private val job = Job()

    override val coroutineContext: CoroutineContext = Dispatchers.Main + job

    companion object {
        private const val immutableFlag = PendingIntent.FLAG_IMMUTABLE
    }

    override fun onCreate() {
        super.onCreate()

        val mediaStoreSource = MediaStoreSourceImpl(this.application)
        val mediaLibrarySource = MediaLibrarySourceImpl(mediaStoreSource)
        librarySessionCallback = CustomMediaLibrarySessionCallback(
            mediaLibrarySource,
            this,
        )

        val player = ExoPlayer.Builder(application)
            .setAudioAttributes(AudioAttributes.DEFAULT, true)
            .setHandleAudioBecomingNoisy(true)
            .build()

        playerWrapper.setUpPlayer(player)
        mediaLibrarySession =
            MediaLibrarySession.Builder(this, player, librarySessionCallback)
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
        mediaLibrarySession.release()

        job.cancel()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaLibrarySession

    private fun getSingleTopActivity(): PendingIntent {
        return PendingIntent.getActivity(
            this,
            0,
            Intent(this, Class.forName("com.andannn.melodify.MainActivity")),
            immutableFlag or PendingIntent.FLAG_UPDATE_CURRENT,
        )
    }
}

private class CustomMediaLibrarySessionCallback(
    private val mediaLibrarySource: MediaLibrarySource,
    private val coroutineScope: CoroutineScope,
) : MediaLibrarySession.Callback, CoroutineScope by coroutineScope {
    override fun onGetLibraryRoot(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        params: LibraryParams?,
    ): ListenableFuture<LibraryResult<MediaItem>> =
        future {
            LibraryResult.ofItem(
                mediaLibrarySource.getLibraryRoot(),
                params,
            )
        }

    override fun onGetChildren(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        parentId: String,
        page: Int,
        pageSize: Int,
        params: LibraryParams?,
    ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> =
        future {
            LibraryResult.ofItemList(
                mediaLibrarySource.getChildren(parentId),
                params,
            )
        }

    override fun onGetItem(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        mediaId: String,
    ): ListenableFuture<LibraryResult<MediaItem>> =
        future {
            mediaLibrarySource.getMediaItem(mediaId)?.let {
                LibraryResult.ofItem(it, null)
            } ?: LibraryResult.ofError(LibraryResult.RESULT_ERROR_NOT_SUPPORTED)
        }

    override fun onAddMediaItems(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: MutableList<MediaItem>,
    ): ListenableFuture<List<MediaItem>> {
        val updatedMediaItems =
            mediaItems.map { mediaItem ->
                mediaItem.buildUpon()
                    .setUri(mediaItem.requestMetadata.mediaUri)
                    .build()
            }
        return Futures.immediateFuture(updatedMediaItems)
    }
}
