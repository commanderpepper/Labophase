package commanderpepper.labophase.screens.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import commanderpepper.labophase.ui.theme.LabophaseTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setContent(
        showingAllLeaders: Boolean = false,
        showingDieRolls: Boolean = false,
        clearHistory: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            LabophaseTheme {
                SettingsScreen(
                    showingAllLeaders = showingAllLeaders,
                    showingDieRolls = showingDieRolls,
                    toggleLeaders = {},
                    toggleDieRoll = {},
                    clearHistory = clearHistory
                )
            }
        }
    }

    @Test
    fun showAllLeadersLabel_isDisplayed() {
        setContent()
        composeTestRule.onNodeWithText("Show all leaders").assertIsDisplayed()
    }

    @Test
    fun showDieRollsLabel_isDisplayed() {
        setContent()
        composeTestRule.onNodeWithText("Show die rolls").assertIsDisplayed()
    }

    @Test
    fun clearEntriesButton_isDisplayed() {
        setContent()
        composeTestRule.onNodeWithText("Clear Entries").assertIsDisplayed()
    }

    @Test
    fun clearEntriesButton_click_invokesClearHistoryCallback() {
        var called = false
        setContent(clearHistory = { called = true })
        composeTestRule.onNodeWithText("Clear Entries").performClick()
        assertTrue(called)
    }
}
