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

internal class PlayerImpl : VlcPlayer, CoroutineScope {

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

    private val _playerStateFlow = MutableStateFlow<PlayerState>(PlayerState.Idle)
    private val _currentPositionFlow = MutableStateFlow(0F)
    private val _playingDurationFlow = MutableStateFlow(0L)
    private val _playingMrlFlow = MutableStateFlow<String?>(null)
    private val _playListFlow = MutableStateFlow<List<String>>(emptyList())

    private val currentPosition: Long =
        (_currentPositionFlow.value * _playingDurationFlow.value).toLong()

    private val playerEventLister = object : MediaPlayerEventAdapter() {
        override fun mediaChanged(mediaPlayer: MediaPlayer, media: MediaRef) {
            launch {
                val newMedia = media.newMedia().info()
                _playingMrlFlow.value = newMedia.mrl()
                Napier.d(tag = TAG) { "media changed duration: ${newMedia.duration()}. thread ${Thread.currentThread()}" }
            }
        }

        override fun lengthChanged(mediaPlayer: MediaPlayer?, newLength: Long) {
            launch {
                Napier.d(tag = TAG) { "lengthChanged duration: ${newLength}. thread ${Thread.currentThread()}" }
                _playingDurationFlow.value = newLength
            }
        }

        override fun paused(mediaPlayer: MediaPlayer?) {
            Napier.d(tag = TAG) { "paused. thread ${Thread.currentThread()}" }

            _playerStateFlow.value = PlayerState.Paused(currentPosition)
        }

        override fun playing(mediaPlayer: MediaPlayer?) {
            Napier.d(tag = TAG) { "playing. thread ${Thread.currentThread()}" }

            _playerStateFlow.value = PlayerState.Playing(currentPosition)
        }

        override fun buffering(mediaPlayer: MediaPlayer?, newCache: Float) {
//            _playerStateFlow.value = PlayerState.Buffering(currentPosition)
        }

        override fun finished(mediaPlayer: MediaPlayer?) {
            Napier.d(tag = TAG) { "finished. thread ${Thread.currentThread()}" }
        }

        override fun positionChanged(mediaPlayer: MediaPlayer?, newPosition: Float) {
            _currentPositionFlow.value = newPosition

            _playerStateFlow.getAndUpdate { old ->
                when (old) {
                    is PlayerState.Error,
                    is PlayerState.PlayBackEnd,
                    PlayerState.Idle -> old

                    is PlayerState.Buffering -> PlayerState.Paused(currentPosition)
                    is PlayerState.Paused -> PlayerState.Paused(currentPosition)
                    is PlayerState.Playing -> PlayerState.Playing(currentPosition)
                }
            }
        }
    }

    private val mediaListEvent = object : MediaListEventAdapter() {
        override fun mediaListItemAdded(mediaList: MediaList, item: MediaRef, index: Int) {
            Napier.d(tag = TAG) { "mediaListItemAdded index $index. thread ${Thread.currentThread()}" }

            launch {
                _playListFlow.value = mediaList.media().mrls()
            }
        }

        override fun mediaListItemDeleted(mediaList: MediaList, item: MediaRef, index: Int) {
            Napier.d(tag = TAG) { "mediaListItemDeleted index $index. thread ${Thread.currentThread()}" }
            launch {
                _playListFlow.value = mediaList.media().mrls()
            }
        }
    }


    init {
        playEventApi.addMediaPlayerEventListener(playerEventLister)
        listEventApi.addMediaListEventListener(mediaListEvent)
    }

    override fun playMediaList(mediaList: List<String>, index: Int) {
        launch {
            mediaListApi.clear()

            mediaList.forEach {
                mediaListApi.add(it)
            }

            mediaPlayerApi.play(mediaList[index])
        }
    }

    override fun observePlayerState() = _playerStateFlow

    override val currentPositionMs: Long get() = currentPosition

    override val currentDurationMs: Long get() = _playingDurationFlow.value

    override val playingIndexInQueue: Int
        get() = _playListFlow.value.indexOf(_playingMrlFlow.value)

    override val playList: List<String>
        get() = _playListFlow.value

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

    override fun seekMediaItem(mediaItemIndex: Int, positionMs: Long) {
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

    override fun addMediaItems(index: Int, mrls: List<String>) {
        launch {
            mrls.forEachIndexed { i, mrl ->
                mediaListApi.insert(index + i, mrl)
            }
        }
    }

    override fun moveMediaItem(from: Int, to: Int) {
        launch {
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

    override fun observePlayListQueue() = _playListFlow

    override fun observePlayingMediaMrl() = _playingMrlFlow

    override fun observeProgressFactor() = _currentPositionFlow

    override fun release() {
        nativeApiDispatcher.close()
        nativeApiDispatcher.cancel()

        playEventApi.removeMediaPlayerEventListener(playerEventLister)
        listEventApi.removeMediaListEventListener(mediaListEvent)
        mediaPlayerComponent.release()
    }
}