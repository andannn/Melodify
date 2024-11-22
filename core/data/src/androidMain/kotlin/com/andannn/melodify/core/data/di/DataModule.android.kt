package com.andannn.melodify.core.data.di

import com.andannn.melodify.core.data.repository.MediaContentRepository
import com.andannn.melodify.core.data.repository.MediaControllerRepository
import com.andannn.melodify.core.data.repository.PlayerStateMonitoryRepository
import com.andannn.melodify.core.data.repository.MediaContentRepositoryImpl
import com.andannn.melodify.core.data.repository.MediaControllerRepositoryImpl
import com.andannn.melodify.core.data.repository.PlayerStateMonitoryRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val dataModule = listOf(
    commonDataModule,
    module {
        singleOf(::MediaControllerRepositoryImpl).bind(MediaControllerRepository::class)
        singleOf(::PlayerStateMonitoryRepositoryImpl).bind(PlayerStateMonitoryRepository::class)
    }
)