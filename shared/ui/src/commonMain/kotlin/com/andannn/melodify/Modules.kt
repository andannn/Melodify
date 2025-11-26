/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify

import com.andannn.melodify.core.data.di.dataModule
import com.andannn.melodify.core.syncer.di.syncerModule
import org.koin.core.module.Module

val modules: List<Module> =
    listOf(
        dataModule,
        syncerModule,
    )
