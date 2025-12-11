/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.syncer.di

import com.andannn.melodify.core.syncer.FakeSyncMediaStoreHandler
import com.andannn.melodify.core.syncer.SyncMediaStoreHandler
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val syncerModule: org.koin.core.module.Module =
    module {
        singleOf(::FakeSyncMediaStoreHandler).bind(SyncMediaStoreHandler::class)
    }
