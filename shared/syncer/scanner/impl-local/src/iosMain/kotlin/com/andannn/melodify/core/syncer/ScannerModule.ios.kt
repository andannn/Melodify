/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.syncer

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val localScannerModule: org.koin.core.module.Module =
    module {
        singleOf(::MPLibraryMediaScanner).bind(MediaLibraryScanner::class)
    }
