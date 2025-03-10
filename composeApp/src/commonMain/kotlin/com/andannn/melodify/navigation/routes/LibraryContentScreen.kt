package com.andannn.melodify.navigation.routes

import com.andannn.melodify.ui.components.common.LibraryContentListScreen
import com.andannn.melodify.ui.components.librarycontentlist.LibraryContentPresenter
import com.andannn.melodify.ui.components.librarycontentlist.LibraryContent
import com.andannn.melodify.ui.components.librarycontentlist.LibraryContentState
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import org.koin.mp.KoinPlatform.getKoin

object LibraryContentPresenterFactory : Presenter.Factory {
    override fun create(
        screen: Screen,
        navigator: Navigator,
        context: CircuitContext
    ): Presenter<*>? {
        return when (screen) {
            is LibraryContentListScreen -> LibraryContentPresenter(
                getKoin().get(),
                navigator,
                screen.datasource
            )
            else -> null
        }
    }
}

object LibraryContentUiFactory : Ui.Factory {
    override fun create(screen: Screen, context: CircuitContext): Ui<*>? {
        return when (screen) {
            is LibraryContentListScreen -> ui<LibraryContentState> { state, modifier ->
                LibraryContent(state, modifier)
            }

            else -> null
        }
    }
}
