/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.player

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import uk.co.caprica.vlcj.media.MediaRef
import uk.co.caprica.vlcj.medialist.MediaList
import uk.co.caprica.vlcj.medialist.MediaListEventAdapter
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.component.AudioListPlayerComponent
import java.util.concurrent.Executors

private const val TAG = "PlayerImpl"

internal class PlayerImpl :
    VlcPlayer,
    CoroutineScope {
    private val nativeApiDispatcher = Executors.newFixedThreadPool(1).asCoroutineDispatcher()
    override val coroutineContext = nativeApiDispatcher + Job()

    private val mediaPlayerComponent = AudioListPlayerComponent()

    private val mediaPlayerApi: uk.co.caprica.vlcj.player.base.MediaApi
        get() = mediaPlayerComponent.mediaPlayer().media()!!

    private val mediaListApi: uk.co.caprica.vlcj.medialist.MediaApi
        get() = mediaPlayerComponent.mediaListPlayer().list().media()!!

    private val listControlsApi: uk.co.caprica.vlcj.player.list.ControlsApi
        get() = mediaPlayerComponent.mediaListPlayer().controls()!!

    private val playControlsApi: uk.co.caprica.vlcj.player.base.ControlsApi
        get() = mediaPlayerComponent.mediaPlayer().controls()!!

    private val playEventApi: uk.co.caprica.vlcj.player.base.EventApi
        get() = mediaPlayerComponent.mediaPlayer().events()!!

    private val listEventApi: uk.co.caprica.vlcj.medialist.EventApi
        get() = mediaPlayerComponent.mediaListPlayer().list().events()

    private val playerStateFlow = MutableStateFlow<PlayerState>(PlayerState.Idle)
    private val currentPositionFlow = MutableStateFlow(0F)
    private val playingDurationFlow = MutableStateFlow(0L)
    private val playingMrlFlow = MutableStateFlow<String?>(null)
    private val playListFlow = MutableStateFlow<List<String>>(emptyList())

    private val currentPosition: Long
        get() = (currentPositionFlow.value * playingDurationFlow.value).toLong()

    private val playerEventLister =
        object : MediaPlayerEventAdapter() {
            override fun mediaChanged(
                mediaPlayer: MediaPlayer,
                media: MediaRef,
            ) {
                launch {
                    val newMedia = media.newMedia().info()
                    playingMrlFlow.value = newMedia.mrl()
                    Napier.d(tag = TAG) { "media changed duration: ${newMedia.duration()}. thread ${Thread.currentThread()}" }
                }
            }

            override fun lengthChanged(
                mediaPlayer: MediaPlayer?,
                newLength: Long,
            ) {
                launch {
                    Napier.d(tag = TAG) { "lengthChanged duration: $newLength. thread ${Thread.currentThread()}" }
                    playingDurationFlow.value = newLength
                }
            }

            override fun paused(mediaPlayer: MediaPlayer?) {
                Napier.d(tag = TAG) { "paused. thread ${Thread.currentThread()}" }

                playerStateFlow.value = PlayerState.Paused(currentPosition)
            }

            override fun playing(mediaPlayer: MediaPlayer?) {
                Napier.d(tag = TAG) { "playing. thread ${Thread.currentThread()}" }

                playerStateFlow.value = PlayerState.Playing(currentPosition)
            }

            override fun buffering(
                mediaPlayer: MediaPlayer?,
                newCache: Float,
            ) {
//            _playerStateFlow.value = PlayerState.Buffering(currentPosition)
            }

            override fun finished(mediaPlayer: MediaPlayer?) {
                Napier.d(tag = TAG) { "finished. thread ${Thread.currentThread()}" }
            }

            override fun positionChanged(
                mediaPlayer: MediaPlayer?,
                newPosition: Float,
            ) {
                Napier.d(tag = TAG) { "positionChanged $newPosition. thread ${Thread.currentThread()}" }
                currentPositionFlow.value = newPosition

                playerStateFlow.getAndUpdate { old ->
                    when (old) {
                        is PlayerState.Error,
                        is PlayerState.PlayBackEnd,
                        PlayerState.Idle,
                        -> old

                        is PlayerState.Buffering -> PlayerState.Paused(currentPosition)
                        is PlayerState.Paused -> PlayerState.Paused(currentPosition)
                        is PlayerState.Playing -> PlayerState.Playing(currentPosition)
                    }
                }
            }
        }

    private val mediaListEvent =
        object : MediaListEventAdapter() {
            override fun mediaListItemAdded(
                mediaList: MediaList,
                item: MediaRef,
                index: Int,
            ) {
                Napier.d(tag = TAG) { "mediaListItemAdded index $index. thread ${Thread.currentThread()}" }

                launch {
                    playListFlow.value = mediaList.media().mrls()
                }
            }

            override fun mediaListItemDeleted(
                mediaList: MediaList,
                item: MediaRef,
                index: Int,
            ) {
                Napier.d(tag = TAG) { "mediaListItemDeleted index $index. thread ${Thread.currentThread()}" }
                launch {
                    playListFlow.value = mediaList.media().mrls()
                }
            }
        }

    init {
        playEventApi.addMediaPlayerEventListener(playerEventLister)
        listEventApi.addMediaListEventListener(mediaListEvent)
    }

    override fun playMediaList(
        mediaList: List<String>,
        index: Int,
    ) {
        launch {
            mediaListApi.clear()
            mediaList.toSet().forEach {
                mediaListApi.add(it)
            }

            mediaPlayerApi.play(mediaList[index])
        }
    }

    override fun observePlayerState() = playerStateFlow

    override val currentPositionMs: Long get() = currentPosition

    override val currentDurationMs: Long get() = playingDurationFlow.value

    override val playingIndexInQueue: Int
        get() = playListFlow.value.indexOf(playingMrlFlow.value)

    override val playList: List<String>
        get() = playListFlow.value

    override fun seekToNext() {
        launch {
            listControlsApi.playNext()
        }
    }

    override fun seekToPrevious() {
        launch {
            listControlsApi.playPrevious()
        }
    }

    override fun seekMediaItem(
        mediaItemIndex: Int,
        positionMs: Long,
    ) {
        launch {
            listControlsApi.play(mediaItemIndex)
            playControlsApi.setPosition(positionMs.toFloat().div(currentDurationMs))
        }
    }

    override fun seekToTime(time: Long) {
        launch {
            Napier.d(tag = TAG) { "seekToTime $time. thread ${Thread.currentThread()}" }
            playControlsApi.setPosition(time.toFloat().div(currentDurationMs))
        }
    }

    override fun setShuffleModeEnabled(enable: Boolean) {
    }

    override fun play() {
        launch {
            Napier.d(tag = TAG) { "play. thread ${Thread.currentThread()}" }
            playControlsApi.play()
        }
    }

    override fun pause() {
        launch {
            Napier.d(tag = TAG) { "pause. thread ${Thread.currentThread()}" }
            playControlsApi.pause()
        }
    }

    override fun addMediaItems(
        index: Int,
        mrls: List<String>,
    ) {
        launch {
            val currentMrls = mediaListApi.mrls()
            Napier.d(tag = TAG) { "addMediaItems, currentMrls: $currentMrls, mrls: inserted $mrls. thread ${Thread.currentThread()}" }
            mrls
                .filter { currentMrls.contains(it).not() }
                .forEachIndexed { i, mrl ->
                    mediaListApi.insert(index + i, mrl)
                }
        }
    }

    override fun moveMediaItem(
        from: Int,
        to: Int,
    ) {
        launch {
            Napier.d(tag = TAG) { "moveMediaItem: from $from, to $to. thread ${Thread.currentThread()}" }
            val fromMrl = mediaListApi.mrl(from)
            mediaListApi.remove(from)
            mediaListApi.insert(to, fromMrl)
        }
    }

    override fun removeMediaItem(index: Int) {
        launch {
            mediaListApi.remove(index)
        }
    }

    override fun observePlayListQueue() = playListFlow

    override fun observePlayingMediaMrl() = playingMrlFlow

    override fun observeProgressFactor() = currentPositionFlow

    override fun release() {
        nativeApiDispatcher.close()
        nativeApiDispatcher.cancel()

        playEventApi.removeMediaPlayerEventListener(playerEventLister)
        listEventApi.removeMediaListEventListener(mediaListEvent)
        mediaPlayerComponent.release()
    }
}
