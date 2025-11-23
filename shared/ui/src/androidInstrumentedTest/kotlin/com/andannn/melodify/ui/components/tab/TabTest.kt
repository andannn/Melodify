package com.andannn.melodify.ui.components.tab

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.TabKind
import com.andannn.melodify.ui.createKoinTestRule
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.koin.mp.KoinPlatform.getKoin

private const val TAG = "TabTest"

class TabTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val koinTestRule = createKoinTestRule()

    private val repository
        get() = getKoin().get<Repository>()

    @Test
    fun default_tab_item_Test() {
        composeTestRule.setContent {
            TabUi()
        }
        Thread.sleep(3000)
        composeTestRule.onNodeWithText("All Songs").assertIsDisplayed()
        composeTestRule.onAllNodesWithTag("TabItem").assertCountEquals(1)
    }

    @Test
    fun add_new_tab() =
        runTest {
            composeTestRule.setContent {
                TabUi()
            }

            repository.addNewCustomTab(
                externalId = "id",
                tabName = "NewTab",
                tabKind = TabKind.VIDEO_BUCKET,
            )

            composeTestRule.awaitIdle()

            composeTestRule.onAllNodesWithTag("TabItem").assertCountEquals(2)
            composeTestRule.onNodeWithText("NewTab").assertIsDisplayed()
        }

    @Test
    fun change_selected_tab() =
        runTest {
            composeTestRule.setContent {
                TabUi()
            }

            repository.addNewCustomTab(
                externalId = "id",
                tabName = "NewTab",
                tabKind = TabKind.VIDEO_BUCKET,
            )
            composeTestRule.onNodeWithText("NewTab").performClick()

            composeTestRule.onNodeWithText("NewTab").assertIsSelected()
        }
}
