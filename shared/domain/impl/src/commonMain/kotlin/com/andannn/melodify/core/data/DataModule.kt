/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data

import com.andannn.melodify.core.data.internal.LyricRepositoryImpl
import com.andannn.melodify.core.data.internal.MediaContentRepositoryImpl
import com.andannn.melodify.core.data.internal.PlayListRepositoryImpl
import com.andannn.melodify.core.data.internal.SleepTimerRepositoryImpl
import com.andannn.melodify.core.data.internal.UserPreferenceRepositoryImpl
import com.andannn.melodify.core.database.di.databaseModule
import com.andannn.melodify.core.datastore.di.userPreferencesModule
import com.andannn.melodify.core.network.di.serviceModule
import com.andannn.melodify.core.platform.platformModule
import com.andannn.melodify.domain.LyricRepository
import com.andannn.melodify.domain.MediaContentRepository
import com.andannn.melodify.domain.PlayListRepository
import com.andannn.melodify.domain.Repository
import com.andannn.melodify.domain.SleepTimerRepository
import com.andannn.melodify.domain.UserPreferenceRepository
import com.andannn.melodify.player.sleepTimerModule
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val domainImplModule =
    module {
        single {
            Repository(
                lyricRepository = get(),
                mediaContentRepository = get(),
                mediaControllerRepository = get(),
                playerStateMonitoryRepository = get(),
                playListRepository = get(),
                sleepTimerRepository = get(),
                userPreferenceRepository = get(),
            )
        }.bind(Repository::class)
        singleOf(::PlayListRepositoryImpl).bind(PlayListRepository::class)
        singleOf(::LyricRepositoryImpl).bind(LyricRepository::class)
        singleOf(::UserPreferenceRepositoryImpl).bind(UserPreferenceRepository::class)
        singleOf(::MediaContentRepositoryImpl).bind(MediaContentRepository::class)
        singleOf(::SleepTimerRepositoryImpl).bind(SleepTimerRepository::class)

        includes(
            sleepTimerModule,
            serviceModule,
            userPreferencesModule,
            databaseModule,
            platformModule,
        )

        includes(playerRepoModule)
    }

internal expect val playerRepoModule: Module
