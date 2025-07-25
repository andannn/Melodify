/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.player.di

import com.andannn.melodify.core.player.SleepTimerController
import com.andannn.melodify.core.player.SleepTimerControllerImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val playerModule: Module =
    module {
        includes(platformPlayerModule)
        singleOf(::SleepTimerControllerImpl).bind(SleepTimerController::class)
    }

internal expect val platformPlayerModule: Module
