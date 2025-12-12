/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.syncer.di

import com.andannn.melodify.core.syncer.MediaLibraryScanner
import com.andannn.melodify.core.syncer.MediaStoreScanner
import com.andannn.melodify.core.syncer.ReSyncMediaStoreHandlerHandlerImpl
import com.andannn.melodify.core.syncer.SyncMediaStoreHandler
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformSyncerModule: Module =
    module {
        singleOf(::MediaStoreScanner).bind(MediaLibraryScanner::class)
        singleOf(::ReSyncMediaStoreHandlerHandlerImpl).bind(SyncMediaStoreHandler::class)
    }
