/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.syncer.di

import com.andannn.melodify.core.syncer.BackgroundSyncMediaStoreHandler
import com.andannn.melodify.core.syncer.BackgroundSyncMediaStoreHandlerHandlerImpl
import com.andannn.melodify.core.syncer.SyncWorkHelper
import com.andannn.melodify.core.syncer.SyncWorkHelperImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformSyncerModule: Module =
    module {
        singleOf(::BackgroundSyncMediaStoreHandlerHandlerImpl).bind(BackgroundSyncMediaStoreHandler::class)
        singleOf(::SyncWorkHelperImpl).bind(SyncWorkHelper::class)
    }
