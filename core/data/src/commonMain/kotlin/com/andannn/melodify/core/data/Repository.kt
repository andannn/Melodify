/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data

import com.andannn.melodify.core.data.internal.LyricRepository
import com.andannn.melodify.core.data.internal.MediaContentRepository
import com.andannn.melodify.core.data.internal.MediaControllerRepository
import com.andannn.melodify.core.data.internal.PlayListRepository
import com.andannn.melodify.core.data.internal.PlayerStateMonitoryRepository
import com.andannn.melodify.core.data.internal.SleepTimerRepository
import com.andannn.melodify.core.data.internal.UserPreferenceRepository

class Repository constructor(
    val lyricRepository: LyricRepository,
    val mediaContentRepository: MediaContentRepository,
    val mediaControllerRepository: MediaControllerRepository,
    val playerStateMonitoryRepository: PlayerStateMonitoryRepository,
    val playListRepository: PlayListRepository,
    val sleepTimerRepository: SleepTimerRepository,
    val userPreferenceRepository: UserPreferenceRepository,
) : LyricRepository by lyricRepository,
    MediaContentRepository by mediaContentRepository,
    MediaControllerRepository by mediaControllerRepository,
    PlayerStateMonitoryRepository by playerStateMonitoryRepository,
    PlayListRepository by playListRepository,
    SleepTimerRepository by sleepTimerRepository,
    UserPreferenceRepository by userPreferenceRepository
