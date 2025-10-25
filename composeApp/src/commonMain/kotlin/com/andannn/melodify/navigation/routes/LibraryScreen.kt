/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.navigation.routes

import com.andannn.melodify.ui.components.common.LibraryScreen
import com.andannn.melodify.ui.components.library.Library
import com.andannn.melodify.ui.components.library.LibraryPresenter
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
        context: CircuitContext,
    ): Presenter<*>? =
        when (screen) {
            is LibraryScreen -> LibraryPresenter(navigator)
            else -> null
        }
}

object LibraryUiFactory : Ui.Factory {
    override fun create(
        screen: Screen,
        context: CircuitContext,
    ): Ui<*>? =
        when (screen) {
            is LibraryScreen ->
                ui<LibraryState> { state, modifier ->
                    Library(state, modifier)
                }

            else -> null
        }
}
