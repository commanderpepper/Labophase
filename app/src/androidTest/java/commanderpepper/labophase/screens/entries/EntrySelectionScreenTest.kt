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
import commanderpepper.labophase.models.Location
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

    private val sampleRounds = listOf(RoundEntrySelectionUI(leader = Leader.RShanks, summary = "R Shanks, W, 1"))

    private val sampleEntry = EntrySelectionUI(
        entryId = 1,
        leader = Leader.UGLuffy,
        wins = 2,
        losses = 1,
        punkRecord = "!PR add\nUG Luffy\nW R Shanks 1st",
        rounds = sampleRounds,
        date = "2026-07-30"
    )

    private val sampleEntryWithTitle = sampleEntry.copy(title = "Seattle Regional")
    private val sampleEntryWithLocation = sampleEntry.copy(locationId = Location.ChronosGamesAndGifts.id)
    private val sampleEntryFull = sampleEntry.copy(title = "Seattle Regional", locationId = Location.ChronosGamesAndGifts.id)

    private fun setContent(entries: List<EntrySelectionUI>) {
        composeTestRule.setContent {
            LabophaseTheme {
                EntrySelectionScreen(
                    entries = entries,
                    isLoading = false,
                    errorMessage = null,
                    onEntrySelect = {},
                    onEntryDelete = {},
                    newEntry = {}
                )
            }
        }
    }

    // region — Loading / Error

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
        composeTestRule.onNodeWithText("New Entry", useUnmergedTree = true).assertExists()
    }

    // endregion

    // region — Entry row label

    @Test
    fun entryDisplayed_noTitle_showsLeaderNameAndDate() {
        setContent(listOf(sampleEntry))
        composeTestRule.onNodeWithText("UG Luffy on Jul 30, 2026 • W: 2 - L: 1").assertIsDisplayed()
    }

    @Test
    fun entryDisplayed_withTitle_showsTitle() {
        setContent(listOf(sampleEntryWithTitle))
        composeTestRule.onNodeWithText("Seattle Regional on Jul 30, 2026 • W: 2 - L: 1").assertIsDisplayed()
    }

    @Test
    fun entryDisplayed_withLocation_showsLocationAbbreviation() {
        setContent(listOf(sampleEntryWithLocation))
        composeTestRule.onNodeWithText("UG Luffy at Chronos on Jul 30, 2026 • W: 2 - L: 1").assertIsDisplayed()
    }

    @Test
    fun entryDisplayed_withTitleAndLocation_showsFullLabel() {
        setContent(listOf(sampleEntryFull))
        composeTestRule.onNodeWithText("Seattle Regional at Chronos on Jul 30, 2026 • W: 2 - L: 1").assertIsDisplayed()
    }

    @Test
    fun entryDisplayed_noDate_omitsDateFromLabel() {
        setContent(listOf(sampleEntry.copy(date = null)))
        composeTestRule.onNodeWithText("UG Luffy • W: 2 - L: 1").assertIsDisplayed()
    }

    // endregion

    // region — Delete dialog

    @Test
    fun deleteIconClick_opensConfirmationDialog() {
        setContent(listOf(sampleEntry))
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

    // endregion
}
