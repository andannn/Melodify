/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.tab

import app.cash.turbine.test
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.ui.core.NoOpPopupController
import com.andannn.melodify.ui.mock.MockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TabUiPresenterTest {
    @Test
    fun tab_list_is_update_when_flow_changed() =
        runTest {
            val repo = TabUiMockRepository(tabOf("A", "B"))

            val presenter = TabUiPresenter(repo, NoOpPopupController())
            presenter.currentTabListFlow.test {
                assertEquals(tabOf("A", "B"), awaitItem())
                repo.changeTab(tabOf("A", "B", "C"))
                assertEquals(tabOf("A", "B", "C"), awaitItem())
            }
        }

    @Test
    fun new_added_tab_selected_when_tab_list_changed() =
        runTest {
            val repo = TabUiMockRepository(tabOf("A", "B"))

            val presenter = TabUiPresenter(repo, NoOpPopupController())
            presenter.selectedIndexFlow.test {
                assertEquals(0, awaitItem())
                repo.changeTab(tabOf("A", "B", "C"))
                assertEquals(2, awaitItem())
            }
        }

    @Test
    fun last_tab_selected_when_last_tab_removed() =
        runTest {
            val repo = TabUiMockRepository(tabOf("A", "B"))

            val presenter = TabUiPresenter(repo, NoOpPopupController())
            presenter.selectedIndexFlow.test {
                assertEquals(0, awaitItem())
                repo.changeTab(tabOf("A", "B", "C"))
                assertEquals(2, awaitItem())
                repo.changeTab(tabOf("A", "B"))
                assertEquals(1, awaitItem())
            }
        }
}

private fun tabOf(vararg tab: String) = tab.map { CustomTab.AlbumDetail(0, albumId = "mock", label = it) }

private class TabUiMockRepository(
    initialTabs: List<CustomTab> = emptyList(),
) : MockRepository() {
    private val tabFlow = MutableStateFlow(initialTabs)
    override val currentCustomTabsFlow: Flow<List<CustomTab>> = tabFlow

    fun changeTab(tabs: List<CustomTab>) {
        tabFlow.value = tabs
    }
}
