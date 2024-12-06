package com.andannn.melodify.core.data.di

import com.andannn.melodify.core.data.repository.LyricRepository
import com.andannn.melodify.core.data.repository.PlayListRepository
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.repository.UserPreferenceRepository
import com.andannn.melodify.core.data.repository.LyricRepositoryImpl
import com.andannn.melodify.core.data.repository.MediaContentRepository
import com.andannn.melodify.core.data.repository.MediaContentRepositoryImpl
import com.andannn.melodify.core.data.repository.PlayListRepositoryImpl
import com.andannn.melodify.core.data.repository.SleepTimerRepository
import com.andannn.melodify.core.data.repository.SleepTimerRepositoryImpl
import com.andannn.melodify.core.data.repository.UserPreferenceRepositoryImpl
import com.andannn.melodify.core.database.di.databaseModule
import com.andannn.melodify.core.datastore.di.userPreferencesModule
import com.andannn.melodify.core.network.di.serviceModule
import com.andannn.melodify.core.player.di.playerModule
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    singleOf(::Repository)
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
    )
}

internal expect val platformDataModule : Module
