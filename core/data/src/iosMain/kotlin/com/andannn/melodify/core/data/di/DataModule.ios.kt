package com.andannn.melodify.core.data.di

import com.andannn.melodify.core.data.repository.MediaContentRepository
import com.andannn.melodify.core.data.repository.MediaControllerRepository
import com.andannn.melodify.core.data.repository.MediaControllerRepositoryImpl
import com.andannn.melodify.core.data.repository.PlayerStateMonitoryRepository
import com.andannn.melodify.core.data.repository.fake.FakePlayerStateMonitoryRepositoryImpl
import com.andannn.melodify.core.data.repository.fake.FakeMediaControllerRepositoryImpl
import com.andannn.melodify.core.data.repository.fake.FakeMediaContentRepositoryImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual val platformDataModule: Module = module {
    singleOf(::FakeMediaContentRepositoryImpl).bind(
        MediaContentRepository::class
    )
    singleOf(::MediaControllerRepositoryImpl).bind(
        MediaControllerRepository::class
    )
    singleOf(::FakePlayerStateMonitoryRepositoryImpl).bind(
        PlayerStateMonitoryRepository::class
    )
}
