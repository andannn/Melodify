package com.andannn.melodify.ui.components.tabmanagement

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.ui.createKoinTestRule
import org.junit.Rule
import org.junit.Test
import org.koin.mp.KoinPlatform.getKoin

private const val TAG = "TabManagmentTest"

class TabManagmentTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val koinTestRule = createKoinTestRule()

    private val repository
        get() = getKoin().get<Repository>()

    @Test
    fun default_tab_item_Test() {
        composeTestRule.setContent {
            TabManagementUi()
        }
        composeTestRule.onNodeWithText("All Songs").assertIsDisplayed()
    }

    @Test
    fun delete_tab_item_Test() {
        composeTestRule.setContent {
            TabManagementUi()
        }
        composeTestRule
            .onNodeWithContentDescription("Delete")
            .performClick()
        composeTestRule.onNodeWithText("All Songs").assertIsNotDisplayed()
    }
}
