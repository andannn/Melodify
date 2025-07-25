/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.syncer.di

import com.andannn.melodify.core.syncer.MediaLibraryScanner
import com.andannn.melodify.core.syncer.MediaLibraryScannerImpl
import com.andannn.melodify.core.syncer.MediaLibrarySyncer
import com.andannn.melodify.core.syncer.MediaLibrarySyncerWrapper
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val syncerModule: Module =
    module {
        singleOf(::MediaLibrarySyncerWrapper).bind(MediaLibrarySyncer::class)
        singleOf(::MediaLibraryScannerImpl).bind(MediaLibraryScanner::class)
    }
