/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify

import com.andannn.melodify.core.data.domainImplModule
import com.andannn.melodify.core.syncer.di.syncerModule
import com.andannn.melodify.domain.MediaFileDeleteHelper
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun startKoinWithPlatformModule(platformModuleProvider: () -> Module) {
    startKoin {
        modules(
            domainImplModule,
            syncerModule,
            extraModel,
            platformModuleProvider(),
        )
    }
}

private val extraModel =
    module {
        singleOf(::MediaFileDeleteHelperImpl).bind(MediaFileDeleteHelper::class)
    }
