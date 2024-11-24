package com.andannn.melodify

import com.andannn.melodify.feature.drawer.DrawerController
import com.andannn.melodify.feature.drawer.DrawerControllerImpl
import com.andannn.melodify.feature.message.MessageController
import com.andannn.melodify.feature.message.MessageControllerImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.scopedOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Use android retained activity scope to hold Ui State class. 
 */
actual val uiScopedModule: Module = module {
    scope<MainActivity> {
        scopedOf(::DrawerControllerImpl).bind(DrawerController::class)
    }
    scope<MainActivity> {
        scopedOf(::MessageControllerImpl).bind(MessageController::class)
    }
}
