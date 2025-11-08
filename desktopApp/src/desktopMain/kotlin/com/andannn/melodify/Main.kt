/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify

import androidx.compose.ui.window.application
import com.andannn.melodify.app.MelodifyDeskTopApp
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun main() =
    application {
        Napier.base(DebugAntilog())

        startKoin {
            modules(
                listOf(
                    extraModel,
                    *modules.toTypedArray(),
                ),
            )
        }

        MelodifyDeskTopApp()
    }

private val extraModel =
    module {
        singleOf(::MediaFileDeleteHelperImpl).bind(MediaFileDeleteHelper::class)
    }
