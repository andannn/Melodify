package com.andannn.melodify

import androidx.compose.ui.window.ComposeUIViewController
import com.andannn.melodify.core.data.MediaFileDeleteHelper
import com.andannn.melodify.core.data.di.dataModule
import com.andannn.melodify.core.syncer.di.syncerModule
import com.andannn.melodify.ui.app.MelodifyMobileApp
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

@Suppress("ktlint:standard:function-naming")
fun MainViewController() =
    ComposeUIViewController(
        configure = {
// TODO: Check debug build
            Napier.base(DebugAntilog())

            startKoin {
                modules(
                    dataModule,
                    syncerModule,
                    extraModel,
                )
            }
        },
    ) {
        MelodifyMobileApp()
    }

private val extraModel =
    module {
        singleOf(::MediaFileDeleteHelperImpl).bind(MediaFileDeleteHelper::class)
    }
