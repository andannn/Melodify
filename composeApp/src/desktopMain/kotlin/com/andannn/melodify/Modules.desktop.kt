package com.andannn.melodify

import com.andannn.melodify.feature.drawer.DrawerController
import com.andannn.melodify.feature.drawer.DrawerControllerImpl
import com.andannn.melodify.feature.message.MessageController
import com.andannn.melodify.feature.message.MessageControllerImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Desktop app have no destroy-reconstruction lifecycle like android.
 * Just use singleton as app state.
 */
actual val uiScopedModule = module {
    singleOf(::DrawerControllerImpl).bind(DrawerController::class)
    singleOf(::MessageControllerImpl).bind(MessageController::class)
}