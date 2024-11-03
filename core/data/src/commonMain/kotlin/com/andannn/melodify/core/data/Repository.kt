package com.andannn.melodify.core.data

import com.andannn.melodify.core.data.repository.LyricRepository
import com.andannn.melodify.core.data.repository.MediaContentRepository
import com.andannn.melodify.core.data.repository.MediaControllerRepository
import com.andannn.melodify.core.data.repository.PlayListRepository
import com.andannn.melodify.core.data.repository.PlayerStateMonitoryRepository
import com.andannn.melodify.core.data.repository.UserPreferenceRepository

class Repository(
    val lyricRepository: LyricRepository,
    val mediaContentRepository: MediaContentRepository,
    val mediaControllerRepository: MediaControllerRepository,
    val playerStateMonitoryRepository: PlayerStateMonitoryRepository,
    val playListRepository: PlayListRepository,
    val userPreferenceRepository: UserPreferenceRepository
)