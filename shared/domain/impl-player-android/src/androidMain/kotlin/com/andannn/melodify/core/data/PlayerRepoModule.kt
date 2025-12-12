/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data

import com.andannn.melodify.core.data.internal.MediaControllerRepositoryImpl
import com.andannn.melodify.core.data.internal.PlayerStateMonitoryRepositoryImpl
import com.andannn.melodify.core.player.di.platformPlayerModule
import com.andannn.melodify.domain.MediaControllerRepository
import com.andannn.melodify.domain.PlayerStateMonitoryRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val androidPlayerRepoModule =
    module {
        singleOf(::MediaControllerRepositoryImpl).bind(MediaControllerRepository::class)
        singleOf(::PlayerStateMonitoryRepositoryImpl).bind(PlayerStateMonitoryRepository::class)

        includes(platformPlayerModule)
    }
