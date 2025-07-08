package com.andannn.melodify.ui.components.tab

import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.core.data.repository.NoOpUserPreferenceRepository
import com.andannn.melodify.ui.components.popup.NoOpPopupController
import com.slack.circuit.test.test
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TabUiPresenterTest {
    private val dispatcher = StandardTestDispatcher()
    private val testScope = TestScope(dispatcher)

    private lateinit var presenter: TabUiPresenter
    private lateinit var dummyUserPreferenceRepository: DummyUserPreferenceRepository

    @BeforeTest
    fun setUp() {
        dummyUserPreferenceRepository = DummyUserPreferenceRepository()
        presenter =
            TabUiPresenter(
                repository =
                    Repository(
                        userPreferenceRepository = dummyUserPreferenceRepository,
                    ),
                popupController = NoOpPopupController(),
            )
    }

    @Test
    fun `test get current tab`() =
        testScope.runTest {
            presenter.test {
                awaitItem()
                    .also { assertEquals(true, it.customTabList.isEmpty()) }
                awaitItem()
                    .also { assertEquals(CustomTab.AllMusic, it.customTabList.first()) }
                    .also { it.eventSink.invoke(TabUiEvent.OnClickTab(0)) }

                dummyUserPreferenceRepository.addTabAtLast(
                    CustomTab.AlbumDetail(
                        albumId = "id",
                        label = "test",
                    ),
                )
                awaitItem()
                    .also { assertEquals(2, it.customTabList.size) }
                    .also { assertTrue { it.customTabList[1] is CustomTab.AlbumDetail } }
                // New added item is selected.
                awaitItem()
                    .also { assertEquals(1, it.selectedIndex) }

                dummyUserPreferenceRepository.deleteLast()
                // selected item is deleted, select the item before it
                awaitItem()
                    .also { assertEquals(1, it.customTabList.size) }
                    .also { assertEquals(0, it.selectedIndex) }
                awaitUnchanged()
            }
        }
}

private class DummyUserPreferenceRepository : NoOpUserPreferenceRepository() {
    private val customTabs = MutableStateFlow(listOf<CustomTab>(CustomTab.AllMusic))

    override val currentCustomTabsFlow = customTabs

    fun addTabAtLast(tab: CustomTab) {
        customTabs.value += tab
    }

    fun deleteLast() {
        customTabs.value = customTabs.value.toMutableList().apply { removeAt(size - 1) }
    }
}
