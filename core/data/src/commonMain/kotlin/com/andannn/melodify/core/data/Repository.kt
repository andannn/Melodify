/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data

import com.andannn.melodify.core.data.repository.LyricRepository
import com.andannn.melodify.core.data.repository.MediaContentRepository
import com.andannn.melodify.core.data.repository.MediaControllerRepository
import com.andannn.melodify.core.data.repository.NoOpLyricRepository
import com.andannn.melodify.core.data.repository.NoOpMediaContentRepository
import com.andannn.melodify.core.data.repository.NoOpMediaControllerRepository
import com.andannn.melodify.core.data.repository.NoOpPlayListRepository
import com.andannn.melodify.core.data.repository.NoOpPlayerStateMonitoryRepository
import com.andannn.melodify.core.data.repository.NoOpSleepTimerRepository
import com.andannn.melodify.core.data.repository.PlayListRepository
import com.andannn.melodify.core.data.repository.PlayerStateMonitoryRepository
import com.andannn.melodify.core.data.repository.SleepTimerRepository
import com.andannn.melodify.core.data.repository.UserPreferenceRepository

class Repository constructor(
    val lyricRepository: LyricRepository = NoOpLyricRepository(),
    val mediaContentRepository: MediaContentRepository = NoOpMediaContentRepository(),
    val mediaControllerRepository: MediaControllerRepository = NoOpMediaControllerRepository(),
    val playerStateMonitoryRepository: PlayerStateMonitoryRepository = NoOpPlayerStateMonitoryRepository(),
    val playListRepository: PlayListRepository = NoOpPlayListRepository(),
    val sleepTimerRepository: SleepTimerRepository = NoOpSleepTimerRepository(),
    val userPreferenceRepository: UserPreferenceRepository,
) : LyricRepository by lyricRepository,
    MediaContentRepository by mediaContentRepository,
    MediaControllerRepository by mediaControllerRepository,
    PlayerStateMonitoryRepository by playerStateMonitoryRepository,
    PlayListRepository by playListRepository,
    SleepTimerRepository by sleepTimerRepository,
    UserPreferenceRepository by userPreferenceRepository
