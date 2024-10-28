package com.andannn.melodify

import android.app.Application
import com.andannn.melodify.feature.common.GlobalUiController
import com.andannn.melodify.feature.common.GlobalUiControllerImpl
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.viewModel
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
                    globalUiControllerModule,
                    *modules.toTypedArray(),
                )
            )
        }
    }
}

private val activityViewModelModule = module {
    viewModel {
        MainActivityViewModel(get(), get())
    }
}

private val globalUiControllerModule = module {
    scope<MainActivity> {
        scopedOf(::GlobalUiControllerImpl).bind(GlobalUiController::class)
    }
}
