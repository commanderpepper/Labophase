package commanderpepper.labophase.screens.entries

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import commanderpepper.labophase.models.Leader
import commanderpepper.labophase.screens.entries.models.EntrySelectionUI
import commanderpepper.labophase.screens.entries.models.RoundEntrySelectionUI
import commanderpepper.labophase.ui.theme.LabophaseTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EntrySelectionScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleEntry = EntrySelectionUI(
        entryId = 1,
        leader = Leader.UGLuffy,
        wins = 2,
        losses = 1,
        punkRecord = "!PR add\nUG Luffy\nW R Shanks 1st",
        rounds = listOf(RoundEntrySelectionUI(leader = Leader.RShanks, summary = "R Shanks, W, 1"))
    )

    @Test
    fun loadingState_showsProgressIndicator() {
        composeTestRule.setContent {
            LabophaseTheme {
                EntrySelectionScreen(
                    entries = emptyList(),
                    isLoading = true,
                    errorMessage = null,
                    onEntrySelect = {},
                    onEntryDelete = {},
                    newEntry = {}
                )
            }
        }
        composeTestRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertExists()
    }

    @Test
    fun errorState_showsErrorMessage() {
        composeTestRule.setContent {
            LabophaseTheme {
                EntrySelectionScreen(
                    entries = emptyList(),
                    isLoading = false,
                    errorMessage = "Something went wrong",
                    onEntrySelect = {},
                    onEntryDelete = {},
                    newEntry = {}
                )
            }
        }
        composeTestRule.onNodeWithText("Something went wrong").assertIsDisplayed()
    }

    @Test
    fun emptyList_showsNewEntryFab() {
        composeTestRule.setContent {
            LabophaseTheme {
                EntrySelectionScreen(
                    entries = emptyList(),
                    isLoading = false,
                    errorMessage = null,
                    onEntrySelect = {},
                    onEntryDelete = {},
                    newEntry = {}
                )
            }
        }
        composeTestRule.onNodeWithText("New Entry").assertIsDisplayed()
    }

    @Test
    fun entryDisplayed_showsWinsAndLosses() {
        composeTestRule.setContent {
            LabophaseTheme {
                EntrySelectionScreen(
                    entries = listOf(sampleEntry),
                    isLoading = false,
                    errorMessage = null,
                    onEntrySelect = {},
                    onEntryDelete = {},
                    newEntry = {}
                )
            }
        }
        composeTestRule.onNodeWithText("W: 2 - L: 1").assertIsDisplayed()
    }

    @Test
    fun deleteIconClick_opensConfirmationDialog() {
        composeTestRule.setContent {
            LabophaseTheme {
                EntrySelectionScreen(
                    entries = listOf(sampleEntry),
                    isLoading = false,
                    errorMessage = null,
                    onEntrySelect = {},
                    onEntryDelete = {},
                    newEntry = {}
                )
            }
        }
        composeTestRule.onNodeWithContentDescription("Delete entry").performClick()
        composeTestRule.onNodeWithText("Delete Entry").assertIsDisplayed()
    }

    @Test
    fun deleteDialog_confirmButton_invokesOnEntryDelete() {
        var deletedId = -1
        composeTestRule.setContent {
            LabophaseTheme {
                EntrySelectionScreen(
                    entries = listOf(sampleEntry),
                    isLoading = false,
                    errorMessage = null,
                    onEntrySelect = {},
                    onEntryDelete = { deletedId = it },
                    newEntry = {}
                )
            }
        }
        composeTestRule.onNodeWithContentDescription("Delete entry").performClick()
        composeTestRule.onNodeWithText("Delete").performClick()
        assertEquals(1, deletedId)
    }

    @Test
    fun deleteDialog_cancelButton_doesNotInvokeOnEntryDelete() {
        var deleteInvoked = false
        composeTestRule.setContent {
            LabophaseTheme {
                EntrySelectionScreen(
                    entries = listOf(sampleEntry),
                    isLoading = false,
                    errorMessage = null,
                    onEntrySelect = {},
                    onEntryDelete = { deleteInvoked = true },
                    newEntry = {}
                )
            }
        }
        composeTestRule.onNodeWithContentDescription("Delete entry").performClick()
        composeTestRule.onNodeWithText("Cancel").performClick()
        assertEquals(false, deleteInvoked)
    }
}
