/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.di

import com.andannn.melodify.core.data.LyricRepository
import com.andannn.melodify.core.data.MediaContentRepository
import com.andannn.melodify.core.data.MediaControllerRepository
import com.andannn.melodify.core.data.PlayListRepository
import com.andannn.melodify.core.data.PlayerStateMonitoryRepository
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.RepositoryImpl
import com.andannn.melodify.core.data.SleepTimerRepository
import com.andannn.melodify.core.data.UserPreferenceRepository
import com.andannn.melodify.core.data.internal.FakeMediaControllerRepository
import com.andannn.melodify.core.data.internal.LyricRepositoryImpl
import com.andannn.melodify.core.data.internal.MediaContentRepositoryImpl
import com.andannn.melodify.core.data.internal.PlayListRepositoryImpl
import com.andannn.melodify.core.data.internal.SleepTimerRepositoryImpl
import com.andannn.melodify.core.data.internal.UserPreferenceRepositoryImpl
import com.andannn.melodify.core.database.di.databaseModule
import com.andannn.melodify.core.database.di.dummyDatabaseModule
import com.andannn.melodify.core.datastore.di.userPreferencesModule
import com.andannn.melodify.core.network.di.dummyServiceModule
import com.andannn.melodify.core.network.di.serviceModule
import com.andannn.melodify.core.platform.platformModule
import com.andannn.melodify.core.player.SleepTimerController
import com.andannn.melodify.core.player.SleepTimerControllerImpl
import com.andannn.melodify.core.player.di.dummyPlayerModule
import com.andannn.melodify.core.player.di.playerModule
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule =
    module {
        singleOf(::RepositoryImpl).bind(Repository::class)
        singleOf(::PlayListRepositoryImpl).bind(PlayListRepository::class)
        singleOf(::LyricRepositoryImpl).bind(LyricRepository::class)
        singleOf(::UserPreferenceRepositoryImpl).bind(UserPreferenceRepository::class)
        singleOf(::MediaContentRepositoryImpl).bind(MediaContentRepository::class)
        singleOf(::SleepTimerRepositoryImpl).bind(SleepTimerRepository::class)
        includes(platformDataModule)

        includes(
            playerModule,
            serviceModule,
            userPreferencesModule,
            databaseModule,
            platformModule,
        )
    }

val dummyDataModel =
    module {
        singleOf(::RepositoryImpl).bind(Repository::class)
        singleOf(::PlayListRepositoryImpl).bind(PlayListRepository::class)
        singleOf(::LyricRepositoryImpl).bind(LyricRepository::class)
        singleOf(::UserPreferenceRepositoryImpl).bind(UserPreferenceRepository::class)
        singleOf(::MediaContentRepositoryImpl).bind(MediaContentRepository::class)
        singleOf(::SleepTimerRepositoryImpl).bind(SleepTimerRepository::class)
        val fakeRepo = FakeMediaControllerRepository()
        single<MediaControllerRepository> { fakeRepo }
        single<PlayerStateMonitoryRepository> { fakeRepo }
        singleOf(::SleepTimerControllerImpl).bind(SleepTimerController::class)

        includes(
            dummyPlayerModule,
            dummyServiceModule,
            userPreferencesModule,
            dummyDatabaseModule,
        )
    }

internal expect val platformDataModule: Module
