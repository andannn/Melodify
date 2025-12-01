/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data

fun Repository(
    lyricRepository: LyricRepository,
    mediaContentRepository: MediaContentRepository,
    mediaControllerRepository: MediaControllerRepository,
    playerStateMonitoryRepository: PlayerStateMonitoryRepository,
    playListRepository: PlayListRepository,
    sleepTimerRepository: SleepTimerRepository,
    userPreferenceRepository: UserPreferenceRepository,
): Repository =
    RepositoryImpl(
        lyricRepository = lyricRepository,
        mediaContentRepository = mediaContentRepository,
        mediaControllerRepository = mediaControllerRepository,
        playerStateMonitoryRepository = playerStateMonitoryRepository,
        playListRepository = playListRepository,
        sleepTimerRepository = sleepTimerRepository,
        userPreferenceRepository = userPreferenceRepository,
    )

interface Repository :
    LyricRepository,
    MediaContentRepository,
    MediaControllerRepository,
    PlayerStateMonitoryRepository,
    PlayListRepository,
    SleepTimerRepository,
    UserPreferenceRepository

internal class RepositoryImpl(
    val lyricRepository: LyricRepository,
    val mediaContentRepository: MediaContentRepository,
    val mediaControllerRepository: MediaControllerRepository,
    val playerStateMonitoryRepository: PlayerStateMonitoryRepository,
    val playListRepository: PlayListRepository,
    val sleepTimerRepository: SleepTimerRepository,
    val userPreferenceRepository: UserPreferenceRepository,
) : Repository,
    LyricRepository by lyricRepository,
    MediaContentRepository by mediaContentRepository,
    MediaControllerRepository by mediaControllerRepository,
    PlayerStateMonitoryRepository by playerStateMonitoryRepository,
    PlayListRepository by playListRepository,
    SleepTimerRepository by sleepTimerRepository,
    UserPreferenceRepository by userPreferenceRepository
