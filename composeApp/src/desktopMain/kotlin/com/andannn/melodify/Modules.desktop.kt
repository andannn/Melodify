package com.andannn.melodify

import com.andannn.melodify.ui.components.popup.PopupController
import com.andannn.melodify.ui.components.popup.PopupControllerImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Desktop app have no destroy-reconstruction lifecycle like android.
 * Just use singleton as app state.
 */
actual val uiScopedModule = module {
    singleOf(::PopupControllerImpl).bind(PopupController::class)
}
