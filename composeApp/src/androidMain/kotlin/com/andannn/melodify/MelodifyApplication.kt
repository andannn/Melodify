/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify

import android.app.Application
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

class MelodifyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Napier.base(DebugAntilog())
        }

        Napier.d { "Application onCreate" }

        startKoin {
            androidContext(this@MelodifyApplication)
            modules(
                listOf(
                    activityViewModelModule,
                    *modules.toTypedArray(),
                ),
            )
        }
    }
}

private val activityViewModelModule =
    module {
        viewModelOf(::MainActivityViewModel)
    }
