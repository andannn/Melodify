package com.andannn.melodify.core.data.repository

import com.andannn.melodify.core.data.MediaControllerRepository
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.PlayMode
import com.andannn.melodify.core.data.util.uri
import kotlinx.coroutines.flow.flow
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVQueuePlayer
import platform.AVFoundation.play
import platform.Foundation.NSURL
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

private const val TAG = "MediaControllerRepository"

internal class MediaControllerRepositoryImpl(
) : MediaControllerRepository {
    private var queuePlayer: AVQueuePlayer? = null

    override val duration: Long?
        get() = 0L

    override fun playMediaList(mediaList: List<AudioItemModel>, index: Int) {
        val items: List<AVPlayerItem> = mediaList.map {
            AVPlayerItem(uRL = NSURL(string = it.uri))
        }

        queuePlayer = AVQueuePlayer(items)

        queuePlayer?.play()
    }

    override fun seekToNext() {
    }

    override fun seekToPrevious() {
    }

    override fun seekMediaItem(mediaItemIndex: Int, positionMs: Long) {
    }

    override fun seekToTime(time: Long) {
    }

    override fun setPlayMode(mode: PlayMode) {
    }

    override fun setShuffleModeEnabled(enable: Boolean) {
    }

    override fun play() {
    }

    override fun pause() {
    }

    override fun addMediaItems(index: Int, mediaItems: List<AudioItemModel>) {
    }

    override fun moveMediaItem(from: Int, to: Int) {
    }

    override fun removeMediaItem(index: Int) {
    }

    override fun isCounting(): Boolean {
        return false
    }

    override fun observeRemainTime() = flow {
        emit(0.milliseconds)
    }

    override fun startSleepTimer(duration: Duration) {
    }

    override fun cancelSleepTimer() {
    }
}