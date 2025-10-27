/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui

import com.andannn.melodify.ui.routes.HomeState
import com.andannn.melodify.ui.routes.HomeUiScreen
import com.andannn.melodify.ui.routes.Library
import com.andannn.melodify.ui.routes.LibraryDetail
import com.andannn.melodify.ui.routes.LibraryDetailScreenPresenter
import com.andannn.melodify.ui.routes.LibraryDetailScreenState
import com.andannn.melodify.ui.routes.LibraryPresenter
import com.andannn.melodify.ui.routes.LibraryState
import com.andannn.melodify.ui.routes.SearchScreen
import com.andannn.melodify.ui.routes.SearchScreenPresenter
import com.andannn.melodify.ui.routes.SearchScreenState
import com.andannn.melodify.ui.routes.TabManagementScreen
import com.andannn.melodify.ui.routes.TabManagementScreenPresenter
import com.andannn.melodify.ui.routes.TabManagementScreenState
import com.andannn.melodify.ui.routes.rememberHomeUiPresenter
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.presenter.presenterOf
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui

object UiFactory : Ui.Factory {
    override fun create(
        screen: Screen,
        context: CircuitContext,
    ): Ui<*>? =
        when (screen) {
            is HomeScreen ->
                ui<HomeState> { state, modifier ->
                    HomeUiScreen(state, modifier)
                }

            is TabManageScreen ->
                ui<TabManagementScreenState> { state, modifier ->
                    TabManagementScreen(state, modifier)
                }

            is LibraryScreen ->
                ui<LibraryState> { state, modifier ->
                    Library(state, modifier)
                }

            is LibraryDetailScreen ->
                ui<LibraryDetailScreenState> { state, modifier ->
                    LibraryDetail(state, modifier)
                }

            is SearchScreen ->
                ui<SearchScreenState> { state, modifier ->
                    SearchScreen(state, modifier)
                }

            else -> null
        }
}

object PresenterFactory : Presenter.Factory {
    override fun create(
        screen: Screen,
        navigator: Navigator,
        context: CircuitContext,
    ): Presenter<*>? =
        when (screen) {
            is HomeScreen ->
                presenterOf {
                    rememberHomeUiPresenter(navigator).present()
                }

            is TabManageScreen -> TabManagementScreenPresenter(navigator)

            is LibraryDetailScreen ->
                presenterOf {
                    LibraryDetailScreenPresenter(
                        dataSource = screen.datasource,
                        navigator = navigator,
                    ).present()
                }

            is LibraryScreen -> LibraryPresenter(navigator)
            is SearchScreen -> SearchScreenPresenter(navigator)

            else -> null
        }
}
