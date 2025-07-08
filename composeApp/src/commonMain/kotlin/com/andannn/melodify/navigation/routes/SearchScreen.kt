/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.navigation.routes

import com.andannn.melodify.ui.components.common.SearchScreen
import com.andannn.melodify.ui.components.search.Search
import com.andannn.melodify.ui.components.search.SearchUiState
import com.andannn.melodify.ui.components.search.rememberSearchUiPresenter
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.presenter.presenterOf
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui

object SearchUiFactory : Ui.Factory {
    override fun create(
        screen: Screen,
        context: CircuitContext,
    ): Ui<*>? {
        return when (screen) {
            is SearchScreen ->
                ui<SearchUiState> { state, modifier ->
                    Search(state, modifier)
                }

            else -> null
        }
    }
}

object SearchPresenterFactory : Presenter.Factory {
    override fun create(
        screen: Screen,
        navigator: Navigator,
        context: CircuitContext,
    ): Presenter<*>? {
        return when (screen) {
            is SearchScreen ->
                presenterOf {
                    rememberSearchUiPresenter(navigator).present()
                }

            else -> null
        }
    }
}
