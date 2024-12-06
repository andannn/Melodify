package com.andannn.melodify

import com.andannn.melodify.ui.components.popup.PopupController
import com.andannn.melodify.ui.components.popup.PopupControllerImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val uiScopedModule: Module = module {
    singleOf(::PopupControllerImpl).bind(PopupController::class)
}
