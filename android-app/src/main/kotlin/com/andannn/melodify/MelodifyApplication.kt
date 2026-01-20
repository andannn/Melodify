/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify

import android.app.Application
import com.andannn.melodify.core.data.domainImplModule
import com.andannn.melodify.core.syncer.SyncerSetupProperty
import com.andannn.melodify.core.syncer.di.syncerModule
import com.andannn.melodify.domain.MediaFileDeleteHelper
import com.andannn.melodify.util.MediaFileDeleteHelperImpl
import com.andannn.melodify.util.volumn.AndroidVolumeController
import com.andannn.melodify.util.volumn.VolumeController
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
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
                buildList {
                    add(extraModule)
                    add(domainImplModule)

                    val property = SyncerSetupProperty.buildPropertyByFlavor(BuildConfig.content)
                    add(syncerModule(property.type))

                    add(
                        module {
                            single { property }
                        },
                    )
                },
            )
        }
    }
}

private val extraModule =
    module {
        singleOf(::MediaFileDeleteHelperImpl).bind(MediaFileDeleteHelper::class)
        singleOf(::AndroidVolumeController).bind(VolumeController::class)
        viewModelOf(::MainActivityViewModel)
    }
