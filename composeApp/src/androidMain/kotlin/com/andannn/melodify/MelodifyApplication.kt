package com.andannn.melodify

import android.app.Application
import com.andannn.melodify.feature.drawer.DrawerController
import com.andannn.melodify.feature.drawer.DrawerControllerImpl
import com.andannn.melodify.feature.message.MessageController
import com.andannn.melodify.feature.message.MessageControllerImpl
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

class MelodifyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Napier.base(DebugAntilog())
        }

        startKoin {
            androidContext(this@MelodifyApplication)
            modules(
                listOf(
                    activityViewModelModule,
                    scopedModule,
                    *modules.toTypedArray(),
                )
            )
        }
    }
}

private val activityViewModelModule = module {
    viewModelOf(::MainActivityViewModel)
}

private val scopedModule = module {
    scope<MainActivity> {
        scopedOf(::DrawerControllerImpl).bind(DrawerController::class)
    }
    scope<MainActivity> {
        scopedOf(::MessageControllerImpl).bind(MessageController::class)
    }
}
