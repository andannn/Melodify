/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.di

import com.andannn.melodify.core.data.MediaControllerRepository
import com.andannn.melodify.core.data.PlayerStateMonitoryRepository
import com.andannn.melodify.core.data.internal.MediaControllerRepositoryImpl
import com.andannn.melodify.core.data.internal.PlayerStateMonitoryRepositoryImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual val platformDataModule: Module =
    module {
        singleOf(::MediaControllerRepositoryImpl).bind(
            MediaControllerRepository::class,
        )
        singleOf(::PlayerStateMonitoryRepositoryImpl).bind(
            PlayerStateMonitoryRepository::class,
        )
    }
