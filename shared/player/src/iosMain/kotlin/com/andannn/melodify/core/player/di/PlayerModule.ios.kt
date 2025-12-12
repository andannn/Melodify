/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.player.di

import com.andannn.melodify.core.player.AvPlayerQueuePlayer
import com.andannn.melodify.core.player.AvPlayerQueuePlayerImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual val platformPlayerModule: Module =
    module {
        singleOf(::AvPlayerQueuePlayerImpl).bind(AvPlayerQueuePlayer::class)
    }

internal actual val dummyPlatformPlayerModule: Module = module { }
