/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.navigation.routes

import com.andannn.melodify.ui.components.common.TabManageScreen
import com.andannn.melodify.ui.components.tabmanagement.TabManagement
import com.andannn.melodify.ui.components.tabmanagement.TabManagementState
import com.andannn.melodify.ui.components.tabmanagement.rememberTabManagementPresenter
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.presenter.presenterOf
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui

object TabManageUiFactory : Ui.Factory {
    override fun create(
        screen: Screen,
        context: CircuitContext,
    ): Ui<*>? =
        when (screen) {
            is TabManageScreen ->
                ui<TabManagementState> { state, modifier ->
                    TabManagement(state, modifier)
                }

            else -> null
        }
}

object TabManagePresenterFactory : Presenter.Factory {
    override fun create(
        screen: Screen,
        navigator: Navigator,
        context: CircuitContext,
    ): Presenter<*>? =
        when (screen) {
            is TabManageScreen ->
                presenterOf {
                    rememberTabManagementPresenter(navigator).present()
                }

            else -> null
        }
}
