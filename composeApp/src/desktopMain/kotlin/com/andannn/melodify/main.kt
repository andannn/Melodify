package com.andannn.melodify

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.andannn.melodify.feature.drawer.DrawerController
import com.andannn.melodify.feature.drawer.DrawerControllerImpl
import com.andannn.melodify.feature.message.MessageController
import com.andannn.melodify.feature.message.MessageControllerImpl
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun main() = application {
    // TODO: Check build Config.
//    if (BuildConfig.DEBUG) {
    Napier.base(DebugAntilog())
//    }

    startKoin {
        modules(
            listOf(
                scopedModule,
                *modules.toTypedArray(),
            )
        )
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Melodify",
    ) {
        Surface(Modifier.fillMaxSize().background(Color.Blue)) {
            Text("AAAAAAAAAAAAAAAA")
        }
    }
}

/**
 * Desktop app have no destroy-reconstruction lifecycle like android.
 * Just use singleton as app state.
 */
private val scopedModule = module {
    singleOf(::DrawerControllerImpl).bind(DrawerController::class)
    singleOf(::MessageControllerImpl).bind(MessageController::class)
}
