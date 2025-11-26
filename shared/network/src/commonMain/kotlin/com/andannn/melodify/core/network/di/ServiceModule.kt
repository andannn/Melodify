/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.network.di

import com.andannn.melodify.core.network.DummyLrclibService
import com.andannn.melodify.core.network.LrclibService
import com.andannn.melodify.core.network.LrclibServiceImpl
import com.andannn.melodify.core.network.clientBuilder
import org.koin.dsl.module

val serviceModule =
    module {
        single<LrclibService> {
            LrclibServiceImpl(
                clientBuilder(),
            )
        }
    }

val dummyServiceModule =
    module {
        single<LrclibService> { DummyLrclibService() }
    }
