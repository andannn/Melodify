/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.navigation.routes

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.andannn.melodify.ui.components.common.LibraryContentListScreen
import com.andannn.melodify.ui.components.librarycontentlist.LibraryContent
import com.andannn.melodify.ui.components.librarycontentlist.LibraryContentState
import com.andannn.melodify.ui.components.librarycontentlist.rememberLibraryContentPresenter
import com.andannn.melodify.ui.components.popup.LocalPopupController
import com.andannn.melodify.ui.components.popup.PopupControllerImpl
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.presenter.presenterOf
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui

object LibraryContentPresenterFactory : Presenter.Factory {
    override fun create(
        screen: Screen,
        navigator: Navigator,
        context: CircuitContext,
    ): Presenter<*>? =
        when (screen) {
            is LibraryContentListScreen ->
                presenterOf {
                    rememberLibraryContentPresenter(
                        dataSource = screen.datasource,
                        navigator = navigator,
                    ).present()
                }
            else -> null
        }
}

object LibraryContentUiFactory : Ui.Factory {
    override fun create(
        screen: Screen,
        context: CircuitContext,
    ): Ui<*>? =
        when (screen) {
            is LibraryContentListScreen ->
                ui<LibraryContentState> { state, modifier ->
                    LibraryContent(state, modifier)
                }

            else -> null
        }
}
