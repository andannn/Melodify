/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.syncer.di

import com.andannn.melodify.core.syncer.SyncLibraryService
import com.andannn.melodify.core.syncer.SyncLibraryServiceImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformSyncerModule: Module =
    module {
        singleOf(::SyncLibraryServiceImpl).bind(SyncLibraryService::class)
    }
