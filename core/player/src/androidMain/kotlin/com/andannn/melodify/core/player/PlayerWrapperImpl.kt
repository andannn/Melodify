package com.andannn.melodify.core.player

import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.onStart

private const val TAG = "PlayerWrapper"

internal class PlayerWrapperImpl : PlayerWrapper {
    private var player: Player? = null

    private val playerStateFlow = MutableStateFlow<PlayerState>(PlayerState.Idle)

    private val playerModeFlow = MutableStateFlow(Player.REPEAT_MODE_ALL)
    private val isShuffleFlow = MutableStateFlow(false)

    private val playingMediaItemStateFlow = MutableSharedFlow<MediaItem?>(1)
    private val playingIndexInQueueFlow = MutableStateFlow<Int?>(null)

    private val playListFlow = MutableSharedFlow<List<MediaItem>>(1)

    private val playerProgressUpdater: CoroutineTimer =
        CoroutineTimer(delayMs = 1000 / 30L) {
            playerStateFlow.getAndUpdate { old ->
                if (player == null) {
                    return@getAndUpdate old
                }

                when (old) {
                    is PlayerState.Error,
                    is PlayerState.PlayBackEnd,
                    PlayerState.Idle,
                    -> old

                    is PlayerState.Buffering -> PlayerState.Paused(player!!.currentPosition)
                    is PlayerState.Paused -> PlayerState.Paused(player!!.currentPosition)
                    is PlayerState.Playing -> PlayerState.Playing(player!!.currentPosition)
                }
            }
        }

    private val playerListener =
        object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                playerStateFlow.getAndUpdate { old ->
                    val position = old.currentPositionMs

                    when (playbackState) {
                        Player.STATE_IDLE -> PlayerState.Idle
                        Player.STATE_BUFFERING -> PlayerState.Buffering(position)
                        Player.STATE_READY -> {
                            if (player!!.isPlaying) {
                                PlayerState.Playing(position)
                            } else {
                                PlayerState.Paused(position)
                            }
                        }

                        Player.STATE_ENDED -> PlayerState.PlayBackEnd(position)
                        else -> error("Impossible")
                    }
                }
            }

            override fun onPlayWhenReadyChanged(
                playWhenReady: Boolean,
                reason: Int,
            ) {
                Napier.d(tag = TAG) { "onPlayWhenReadyChanged: playWhenReady $playWhenReady reason $reason" }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                Napier.d(tag = TAG) { "onIsPlayingChanged: $isPlaying" }
                with(player!!) {
                    if (isPlaying) {
                        playerStateFlow.value = PlayerState.Playing(currentPosition)
                    } else {
                        val suppressed =
                            playbackSuppressionReason != Player.PLAYBACK_SUPPRESSION_REASON_NONE
                        val playerError = playerError != null
                        if (playbackState == Player.STATE_READY && !suppressed && !playerError) {
                            playerStateFlow.value = PlayerState.Paused(currentPosition)
                        }
                    }
                }
            }

            override fun onPlayerError(error: PlaybackException) {
// TODO: Define exception type and send back.
                playerStateFlow.value =
                    PlayerState.Error(
                        throwable = error,
                        currentPositionMs = player!!.currentPosition,
                    )
            }

            override fun onMediaItemTransition(
                mediaItem: MediaItem?,
                reason: Int,
            ) {
                Napier.d(tag = TAG) { "onMediaItemTransition: $mediaItem" }
                playingIndexInQueueFlow.value = player!!.currentMediaItemIndex
                playingMediaItemStateFlow.tryEmit(mediaItem)
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int,
            ) {
                playerStateFlow.getAndUpdate { state ->
                    when (state) {
                        is PlayerState.Playing -> {
                            state.copy(currentPositionMs = newPosition.contentPositionMs)
                        }

                        is PlayerState.Paused -> {
                            state.copy(currentPositionMs = newPosition.contentPositionMs)
                        }

                        else -> state
                    }
                }
            }

            override fun onTimelineChanged(
                timeline: Timeline,
                reason: Int,
            ) {
                super.onTimelineChanged(timeline, reason)

                MutableList(timeline.windowCount) { index ->
                    timeline.getWindow(index, Timeline.Window()).mediaItem
                }.also { mediaItems ->
                    playListFlow.tryEmit(mediaItems.toList())
                }
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                Napier.d(tag = TAG) { "onShuffleModeEnabledChanged: $shuffleModeEnabled" }
                isShuffleFlow.value = shuffleModeEnabled
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                Napier.d(tag = TAG) { "onRepeatModeChanged: $repeatMode" }
                playerModeFlow.value = repeatMode
            }
        }

    override fun setUpPlayer(player: Player) {
        Napier.d(tag = TAG) { "setUpPlayer" }
        player.prepare()
        player.addListener(playerListener)
        player.repeatMode = Player.REPEAT_MODE_ALL
        playerProgressUpdater.startTicker()

        this.player = player
    }

    override fun release() {
        Napier.d(tag = TAG) { "release" }
        playerStateFlow.value = PlayerState.Idle
        playerModeFlow.value = Player.REPEAT_MODE_ALL
        isShuffleFlow.value = false
        playingIndexInQueueFlow.value = null
        playerProgressUpdater.stopTicker()

        player?.release()
        player = null
    }

    override val currentPositionMs: Long
        get() = player?.currentPosition ?: 0
    override val currentDurationMs: Long
        get() = player?.duration ?: 0

    override val playerState: PlayerState
        get() = playerStateFlow.value

    override val playingIndexInQueue: Int
        get() = playingIndexInQueueFlow.value!!

    override fun observePlayerState(): StateFlow<PlayerState> = playerStateFlow

    override val playList: List<MediaItem>
        get() {
            val timeline = player?.currentTimeline ?: return emptyList()
            return MutableList(timeline.windowCount) { index ->
                timeline.getWindow(index, Timeline.Window()).mediaItem
            }
        }

    override fun observePlayListQueue() =
        playListFlow
            .onStart { playList }

    override fun observePlayingMedia() = playingMediaItemStateFlow.onStart { emit(null) }

    override fun observeIsShuffle() = isShuffleFlow

    override fun observePlayMode() = playerModeFlow
}
