/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.network.di

import com.andannn.melodify.core.network.LrclibService
import com.andannn.melodify.core.network.LrclibServiceImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val lrcServiceModule =
    module {
        singleOf(::LrclibServiceImpl).bind(LrclibService::class)
    }
