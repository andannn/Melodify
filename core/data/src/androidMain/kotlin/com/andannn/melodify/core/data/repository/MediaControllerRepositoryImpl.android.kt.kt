package com.andannn.melodify.core.data.repository

import android.util.Log
import androidx.media3.common.C
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.PlayMode
import com.andannn.melodify.core.player.MediaBrowserManager
import com.andannn.melodify.core.player.SleepTimeCounterState
import com.andannn.melodify.core.player.SleepTimerController
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.takeWhile
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private const val TAG = "MediaControllerRepository"

internal class MediaControllerRepositoryImpl(
    private val mediaBrowserManager: MediaBrowserManager,
    private val sleepTimerController: SleepTimerController,
) : MediaControllerRepository {

    private val mediaBrowser
        get() = mediaBrowserManager.mediaBrowser

    override val currentDuration: Long
        get() = mediaBrowser.duration

    override fun playMediaList(mediaList: List<AudioItemModel>, index: Int) {
        with(mediaBrowser) {
            setMediaItems(
                mediaList.map { it.toMediaItem(generateUniqueId = true) },
                index,
                C.TIME_UNSET,
            )
            prepare()
            play()
        }
    }

    override fun seekToNext() {
        mediaBrowser.seekToNext()
    }

    override fun seekToPrevious() {
        mediaBrowser.seekToPrevious()
    }

    override fun seekMediaItem(mediaItemIndex: Int, positionMs: Long) {
        mediaBrowser.seekTo(mediaItemIndex, positionMs)
    }

    override fun seekToTime(time: Long) {
        mediaBrowser.seekTo(time)
    }

    override fun setPlayMode(mode: PlayMode) {
        mediaBrowser.repeatMode = mode.toExoPlayerMode()
    }

    override fun setShuffleModeEnabled(enable: Boolean) {
        mediaBrowser.shuffleModeEnabled = enable
    }

    override fun play() {
        mediaBrowser.play()
    }

    override fun pause() {
        mediaBrowser.pause()
    }

    override fun addMediaItems(index: Int, mediaItems: List<AudioItemModel>) {
        Log.d(TAG, "addMediaItems: index $index, mediaItems $mediaItems")
        mediaBrowser.addMediaItems(
            /* index = */ index,
            /* mediaItems = */ mediaItems.map { it.toMediaItem(generateUniqueId = true) }
        )
    }

    override fun moveMediaItem(from: Int, to: Int) {
        mediaBrowser.moveMediaItem(from, to)
    }

    override fun removeMediaItem(index: Int) {
        mediaBrowser.removeMediaItem(index)
    }

}