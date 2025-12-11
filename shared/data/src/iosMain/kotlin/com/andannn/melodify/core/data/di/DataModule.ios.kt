/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.di

import com.andannn.melodify.core.data.MediaControllerRepository
import com.andannn.melodify.core.data.PlayerStateMonitoryRepository
import com.andannn.melodify.core.data.internal.FakeMediaControllerRepository
import org.koin.dsl.module

internal actual val platformDataModule: org.koin.core.module.Module =
    module {
        val fakeRepo = FakeMediaControllerRepository()
        single<MediaControllerRepository> { fakeRepo }
        single<PlayerStateMonitoryRepository> { fakeRepo }
    }
