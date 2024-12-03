package com.andannn.melodify

import com.andannn.melodify.ui.components.menu.MenuController
import com.andannn.melodify.ui.components.menu.MenuControllerImpl
import com.andannn.melodify.ui.components.message.MessageController
import com.andannn.melodify.ui.components.message.MessageControllerImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Desktop app have no destroy-reconstruction lifecycle like android.
 * Just use singleton as app state.
 */
actual val uiScopedModule = module {
    singleOf(::MenuControllerImpl).bind(MenuController::class)
    singleOf(::MessageControllerImpl).bind(MessageController::class)
}
