package com.andannn.melodify

import com.andannn.melodify.ui.components.popup.PopupController
import com.andannn.melodify.ui.components.popup.PopupControllerImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.scopedOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Use android retained activity scope to hold Ui State class. 
 */
actual val uiScopedModule: Module = module {
    scope<MainActivity> {
        scopedOf(::PopupControllerImpl).bind(PopupController::class)
    }
}
