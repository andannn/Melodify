/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.syncer.di

import com.andannn.melodify.core.syncer.MediaLibrarySyncRepository
import com.andannn.melodify.core.syncer.MediaLibrarySyncRepositoryImpl
import com.andannn.melodify.core.syncer.MediaLibrarySyncer
import com.andannn.melodify.core.syncer.MediaLibrarySyncerWrapper
import com.andannn.melodify.core.syncer.ScannerType
import com.andannn.melodify.core.syncer.localScannerModule
import com.andannn.melodify.core.syncer.scanner.impl.siren.monsterSirenScannerModule
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal expect val platformSyncerModule: Module

val syncerModule: (ScannerType) -> Module = { scannerType ->
    module {
        includes(platformSyncerModule)
        when (scannerType) {
            ScannerType.LOCAL -> includes(localScannerModule)
            ScannerType.MONSTER_SIREN -> includes(monsterSirenScannerModule)
        }
        singleOf(::MediaLibrarySyncerWrapper).bind(MediaLibrarySyncer::class)
        singleOf(::MediaLibrarySyncRepositoryImpl).bind(MediaLibrarySyncRepository::class)
    }
}
