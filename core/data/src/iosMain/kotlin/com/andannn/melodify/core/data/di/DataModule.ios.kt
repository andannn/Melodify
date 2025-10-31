/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.di

import com.andannn.melodify.core.data.internal.MediaContentRepository
import com.andannn.melodify.core.data.internal.MediaControllerRepository
import com.andannn.melodify.core.data.internal.MediaControllerRepositoryImpl
import com.andannn.melodify.core.data.internal.PlayerStateMonitoryRepository
import com.andannn.melodify.core.data.internal.fake.FakeMediaContentRepositoryImpl
import com.andannn.melodify.core.data.internal.fake.FakePlayerStateMonitoryRepositoryImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual val platformDataModule: Module =
    module {
        singleOf(::FakeMediaContentRepositoryImpl).bind(
            MediaContentRepository::class,
        )
        singleOf(::MediaControllerRepositoryImpl).bind(
            MediaControllerRepository::class,
        )
        singleOf(::FakePlayerStateMonitoryRepositoryImpl).bind(
            PlayerStateMonitoryRepository::class,
        )
    }
