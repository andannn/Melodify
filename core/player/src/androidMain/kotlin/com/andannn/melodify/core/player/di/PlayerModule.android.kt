/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.player.di

import com.andannn.melodify.core.player.MediaBrowserManager
import com.andannn.melodify.core.player.MediaBrowserManagerImpl
import com.andannn.melodify.core.player.PlayerWrapper
import com.andannn.melodify.core.player.PlayerWrapperImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual val platformPlayerModule: Module =
    module {
        singleOf(::PlayerWrapperImpl).bind(PlayerWrapper::class)
        singleOf(::MediaBrowserManagerImpl).bind(MediaBrowserManager::class)
    }
