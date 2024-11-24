package com.andannn.melodify.core.player

sealed class PlayerState(
    open val currentPositionMs: Long
) {
    data object Idle : PlayerState(0)

    data class Buffering(override val currentPositionMs: Long) : PlayerState(currentPositionMs)

    data class Playing(override val currentPositionMs: Long) : PlayerState(currentPositionMs)

    data class Paused(override val currentPositionMs: Long) : PlayerState(currentPositionMs)

    data class PlayBackEnd(override val currentPositionMs: Long) : PlayerState(currentPositionMs)

    data class Error(override val currentPositionMs: Long, val throwable: Throwable) :
        PlayerState(currentPositionMs)
}
