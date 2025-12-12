package com.andannn.melodify.core.player

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class AvPlayerQueuePlayerImpl(
    private val avPlayer: AVPlayerWrapper,
) : AvPlayerQueuePlayer {
    private val scope: CoroutineScope = MainScope()
    private val mutex = Mutex()

    // 播放队列
    private val _playList = MutableStateFlow<List<String>>(emptyList())
    override val playList: List<String> get() = _playList.value

    // 当前 index
    private var currentIndex = -1
    override val playingIndexInQueue: Int get() = currentIndex

    private val playingMedia = MutableStateFlow<String?>(null)

    override fun observePlayingMedia(): Flow<String?> = playingMedia

    private val playerState = MutableStateFlow<PlayerState>(PlayerState.Idle)

    override fun observePlayerState(): StateFlow<PlayerState> = playerState.asStateFlow()

    private val progressFactor = MutableStateFlow(0f)

    override fun observeProgressFactor(): Flow<Float> = progressFactor

    override fun observePlayListQueue(): Flow<List<String>> = _playList.asStateFlow()

    // 进度缓存
    private var currentPosition = 0L
    private var currentDuration = 0L
    override val currentPositionMs: Long get() = currentPosition
    override val currentDurationMs: Long get() = currentDuration

    private var shuffleEnabled = false

    init {
        // Swift 侧通知“播放完成”
        avPlayer.onCompleted = {
            scope.launch { handleTrackCompleted() }
        }

        // Swift 侧进度更新
        avPlayer.onProgress = { pos, dur ->
            currentPosition = pos
            currentDuration = dur
            if (dur > 0) {
                progressFactor.value = pos.toFloat() / dur.toFloat()
            }

            // 播放进度变化时更新 State
            val old = playerState.value
            playerState.value =
                when (old) {
                    is PlayerState.Playing -> PlayerState.Playing(currentPosition)
                    is PlayerState.Paused -> PlayerState.Paused(currentPosition)
                    is PlayerState.Buffering -> PlayerState.Buffering(currentPosition)
                    is PlayerState.Error -> old.copy(currentPositionMs = currentPosition)
                    else -> old
                }
        }
    }

    // 播放整个列表
    override fun playMediaList(
        mediaList: List<String>,
        index: Int,
    ) {
        scope.launch {
            mutex.withLock {
                _playList.value = mediaList
                currentIndex = index.coerceIn(mediaList.indices)
                playCurrent(0)
            }
        }
    }

    private fun playCurrent(startMs: Long) {
        val list = _playList.value
        if (currentIndex !in list.indices) {
            stopPlayback()
            return
        }

        val url = list[currentIndex]
        playingMedia.value = url

        playerState.value = PlayerState.Buffering(currentPosition)

        avPlayer.playUrl(url)

        if (startMs > 0) avPlayer.seekTo(startMs)

        playerState.value = PlayerState.Playing(0)
    }

    override fun seekToNext() {
        scope.launch {
            mutex.withLock {
                val list = _playList.value
                if (list.isEmpty()) return@withLock

                currentIndex =
                    if (shuffleEnabled) {
                        (list.indices).random()
                    } else {
                        if (currentIndex + 1 >= list.size) return@withLock
                        currentIndex + 1
                    }
                playCurrent(0)
            }
        }
    }

    override fun seekToPrevious() {
        scope.launch {
            mutex.withLock {
                val list = _playList.value
                if (list.isEmpty()) return@withLock

                currentIndex =
                    if (shuffleEnabled) {
                        (list.indices).random()
                    } else {
                        if (currentIndex - 1 < 0) return@withLock
                        currentIndex - 1
                    }
                playCurrent(0)
            }
        }
    }

    override fun seekMediaItem(
        mediaItemIndex: Int,
        positionMs: Long,
    ) {
        scope.launch {
            mutex.withLock {
                if (mediaItemIndex !in _playList.value.indices) return@withLock
                currentIndex = mediaItemIndex
                playCurrent(positionMs)
            }
        }
    }

    override fun seekToTime(time: Long) {
        avPlayer.seekTo(time)
        currentPosition = time

        val old = playerState.value
        playerState.value =
            when (old) {
                is PlayerState.Playing -> PlayerState.Playing(time)
                is PlayerState.Paused -> PlayerState.Paused(time)
                else -> old
            }
    }

    override fun play() {
        avPlayer.resume()
        val old = playerState.value
        playerState.value = PlayerState.Playing(old.currentPositionMs)
    }

    override fun pause() {
        avPlayer.pause()
        val old = playerState.value
        playerState.value = PlayerState.Paused(old.currentPositionMs)
    }

    override fun setShuffleModeEnabled(enable: Boolean) {
        shuffleEnabled = enable
    }

    override fun addMediaItems(
        index: Int,
        mrls: List<String>,
    ) {
        scope.launch {
            mutex.withLock {
                val list = _playList.value.toMutableList()
                val insertIndex = index.coerceIn(0, list.size)
                list.addAll(insertIndex, mrls)
                _playList.value = list
            }
        }
    }

    override fun moveMediaItem(
        from: Int,
        to: Int,
    ) {
        scope.launch {
            mutex.withLock {
                val list = _playList.value.toMutableList()
                if (from !in list.indices || to !in list.indices) return@withLock
                val item = list.removeAt(from)
                list.add(to, item)
                _playList.value = list
            }
        }
    }

    override fun removeMediaItem(index: Int) {
        scope.launch {
            mutex.withLock {
                val list = _playList.value.toMutableList()
                if (index !in list.indices) return@withLock

                list.removeAt(index)
                _playList.value = list

                when {
                    list.isEmpty() -> {
                        currentIndex = -1
                        stopPlayback()
                    }

                    index < currentIndex -> {
                        currentIndex--
                    }

                    index == currentIndex -> {
                        if (currentIndex >= list.size) {
                            currentIndex = list.lastIndex
                        }
                        playCurrent(0)
                    }
                }
            }
        }
    }

    override fun release() {
        stopPlayback()
        avPlayer.stop()
        avPlayer.onCompleted = null
        avPlayer.onProgress = null
    }

    private fun stopPlayback() {
        avPlayer.stop()
        currentPosition = 0
        currentDuration = 0
        progressFactor.value = 0f
        playerState.value = PlayerState.Idle
        playingMedia.value = null
    }

    private suspend fun handleTrackCompleted() {
        mutex.withLock {
            val list = _playList.value
            if (list.isEmpty()) {
                playerState.value = PlayerState.PlayBackEnd(0)
                return
            }

            if (currentIndex + 1 < list.size) {
                currentIndex++
                playCurrent(0)
            } else {
                // 队列结束
                stopPlayback()
                playerState.value = PlayerState.PlayBackEnd(currentPosition)
            }
        }
    }
}
