package com.andannn.melodify

import com.andannn.melodify.ui.common.components.drawer.DrawerController
import com.andannn.melodify.ui.common.components.drawer.DrawerControllerImpl
import com.andannn.melodify.ui.common.components.message.MessageController
import com.andannn.melodify.ui.common.components.message.MessageControllerImpl
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
