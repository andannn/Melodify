/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.syncer.scanner.impl.siren

import com.andannn.melodify.core.syncer.MediaLibraryScanner
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val monsterSirenScannerModule: Module =
    module {
        singleOf(::MonsterSirenScanner).bind<MediaLibraryScanner>()
    }
