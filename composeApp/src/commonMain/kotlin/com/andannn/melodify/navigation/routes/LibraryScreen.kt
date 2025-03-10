package com.andannn.melodify.navigation.routes

import com.andannn.melodify.ui.components.library.LibraryPresenter
import com.andannn.melodify.ui.components.library.LibraryScreen
import com.andannn.melodify.ui.components.library.LibraryState
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui

object LibraryPresenterFactory : Presenter.Factory {
    override fun create(
        screen: Screen,
        navigator: Navigator,
        context: CircuitContext
    ): Presenter<*>? {
        return when (screen) {
            is LibraryScreen -> LibraryPresenter()
            else -> null
        }
    }
}

object LibraryUiFactory : Ui.Factory {
    override fun create(screen: Screen, context: CircuitContext): Ui<*>? {
        return when (screen) {
            is LibraryScreen -> ui<LibraryState> { state, modifier ->
                LibraryScreen(state, modifier)
            }

            else -> null
        }
    }
}
