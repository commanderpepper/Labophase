package commanderpepper.labophase.screens.roundentry

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import commanderpepper.labophase.models.Leader
import commanderpepper.labophase.models.Location
import commanderpepper.labophase.models.RoundResult
import commanderpepper.labophase.models.TurnOrder
import commanderpepper.labophase.screens.roundentry.models.RoundDraft
import commanderpepper.labophase.screens.roundentry.models.RoundUI
import commanderpepper.labophase.ui.theme.LabophaseTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoundEntryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleRound = RoundUI(
        roundId = 1,
        leader = Leader.RShanks,
        summary = "R Shanks, W, 1",
        roundResult = RoundResult.Win,
        turnOrder = TurnOrder.First
    )

    private fun setIdleContent(
        rounds: List<RoundUI> = emptyList(),
        punkRecordEntry: String = "",
        title: String? = null,
        date: String = "2026-07-30",
        locationId: Int? = null
    ) {
        composeTestRule.setContent {
            LabophaseTheme {
                RoundEntryScreen(
                    onBack = {},
                    leaderSelected = Leader.UGLuffy,
                    rounds = rounds,
                    playerLeaderList = emptyList(),
                    roundLeaderList = emptyList(),
                    punkRecordEntry = punkRecordEntry,
                    isLoading = false,
                    errorMessage = null,
                    title = title,
                    date = date,
                    locationId = locationId,
                    transformEntry = {},
                    chooseLeader = {},
                    saveRound = { _, _ -> },
                    removeRound = {}
                )
            }
        }
    }

    // region — Loading / Error

    @Test
    fun loadingState_showsProgressIndicator() {
        composeTestRule.setContent {
            LabophaseTheme {
                RoundEntryScreen(
                    onBack = {},
                    leaderSelected = Leader.UGLuffy,
                    rounds = emptyList(),
                    playerLeaderList = emptyList(),
                    roundLeaderList = emptyList(),
                    punkRecordEntry = "",
                    isLoading = true,
                    errorMessage = null,
                    transformEntry = {},
                    chooseLeader = {},
                    saveRound = { _, _ -> },
                    removeRound = {}
                )
            }
        }
        composeTestRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertExists()
    }

    @Test
    fun errorState_showsErrorMessage() {
        composeTestRule.setContent {
            LabophaseTheme {
                RoundEntryScreen(
                    onBack = {},
                    leaderSelected = Leader.UGLuffy,
                    rounds = emptyList(),
                    playerLeaderList = emptyList(),
                    roundLeaderList = emptyList(),
                    punkRecordEntry = "",
                    isLoading = false,
                    errorMessage = "Something went wrong",
                    transformEntry = {},
                    chooseLeader = {},
                    saveRound = { _, _ -> },
                    removeRound = {}
                )
            }
        }
        composeTestRule.onNodeWithText("Something went wrong").assertIsDisplayed()
    }

    // endregion

    // region — Buttons / Navigation

    @Test
    fun idleState_showsSavePunkRecordAndNewRoundButtons() {
        setIdleContent()
        composeTestRule.onNodeWithText("Save punk record entry").assertIsDisplayed()
        composeTestRule.onNodeWithText("New round").assertIsDisplayed()
    }

    @Test
    fun idleState_backNavigationIconIsDisplayed() {
        setIdleContent()
        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
    }

    // endregion

    // region — Row label

    // The name also appears in the top bar (leader) or the title field (title), so check the header row itself
    private fun assertHeaderShows(vararg texts: String) {
        val header = composeTestRule.onNodeWithTag(ENTRY_HEADER_TEST_TAG)
        header.assertIsDisplayed()
        texts.forEach { header.assert(hasText(it)) }
    }

    @Test
    fun rowLabel_noTitle_showsLeaderNameInHeadline() {
        setIdleContent()
        assertHeaderShows("UG Luffy", "on Jul 30, 2026", "W: 0 - L: 0")
    }

    @Test
    fun rowLabel_withTitle_showsTitleInHeadline() {
        setIdleContent(title = "Seattle Regional")
        assertHeaderShows("Seattle Regional", "on Jul 30, 2026", "W: 0 - L: 0")
    }

    @Test
    fun rowLabel_withLocation_showsLocationInOverline() {
        setIdleContent(locationId = Location.ChronosGamesAndGifts.id)
        assertHeaderShows("UG Luffy", "Chronos on Jul 30, 2026", "W: 0 - L: 0")
    }

    @Test
    fun rowLabel_withTitleAndLocation_showsTitleInHeadlineAndLocationInOverline() {
        setIdleContent(title = "Seattle Regional", locationId = Location.ChronosGamesAndGifts.id)
        assertHeaderShows("Seattle Regional", "Chronos on Jul 30, 2026", "W: 0 - L: 0")
    }

    @Test
    fun rowLabel_showsFormattedDate() {
        setIdleContent(date = "2026-07-30")
        composeTestRule.onNodeWithText("on Jul 30, 2026").assertIsDisplayed()
    }

    @Test
    fun rowLabel_showsWinsAndLosses() {
        setIdleContent(rounds = listOf(sampleRound))
        composeTestRule.onNodeWithText("W: 1 - L: 0").assertIsDisplayed()
    }

    // endregion

    // region — Punk Record

    @Test
    fun punkRecordHeader_visibleWhenPunkRecordEntryIsNonEmpty() {
        setIdleContent(punkRecordEntry = "!PR add\nUG Luffy\nW R Shanks 1st")
        composeTestRule.onNodeWithText("Punk Record").assertIsDisplayed()
    }

    // endregion

    // region — Round editor sheet

    private fun setSheetContent(
        rounds: List<RoundUI> = emptyList(),
        saveRound: (Int?, RoundDraft) -> Unit = { _, _ -> },
        removeRound: (Int) -> Unit = {}
    ) {
        composeTestRule.setContent {
            LabophaseTheme {
                RoundEntryScreen(
                    leaderSelected = Leader.UGLuffy,
                    rounds = rounds,
                    playerLeaderList = emptyList(),
                    roundLeaderList = listOf(Leader.RShanks, Leader.GYRobin),
                    punkRecordEntry = "",
                    isLoading = false,
                    errorMessage = null,
                    transformEntry = {},
                    chooseLeader = {},
                    saveRound = saveRound,
                    removeRound = removeRound
                )
            }
        }
    }

    @Test
    fun roundRow_showsEditButtonInsteadOfDelete() {
        setSheetContent(rounds = listOf(sampleRound))
        composeTestRule.onNodeWithContentDescription("Edit round").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Delete").assertDoesNotExist()
    }

    @Test
    fun newRound_opensSheetWithoutDelete() {
        setSheetContent()
        composeTestRule.onNodeWithText("New round").performClick()
        composeTestRule.onNodeWithText("Save").assertIsDisplayed()
        composeTestRule.onNodeWithText("Delete").assertDoesNotExist()
    }

    @Test
    fun newRound_save_addsRoundWithNullId() {
        var saved: Pair<Int?, RoundDraft>? = null
        setSheetContent(saveRound = { id, draft -> saved = id to draft })
        composeTestRule.onNodeWithText("New round").performClick()
        composeTestRule.onNodeWithText("2nd").performClick()
        composeTestRule.onNodeWithText("Save").performClick()
        composeTestRule.waitUntil { saved != null }
        assertEquals(null, saved!!.first)
        assertEquals(TurnOrder.Second, saved!!.second.turnOrder)
    }

    @Test
    fun newRound_cancel_savesNothing() {
        var saveCalls = 0
        setSheetContent(saveRound = { _, _ -> saveCalls++ })
        composeTestRule.onNodeWithText("New round").performClick()
        composeTestRule.onNodeWithText("Cancel").performClick()
        composeTestRule.waitUntil { composeTestRule.onAllNodesWithText("Cancel").fetchSemanticsNodes().isEmpty() }
        assertEquals(0, saveCalls)
    }

    @Test
    fun editRound_save_passesRoundIdAndExistingValues() {
        var saved: Pair<Int?, RoundDraft>? = null
        setSheetContent(rounds = listOf(sampleRound), saveRound = { id, draft -> saved = id to draft })
        composeTestRule.onNodeWithContentDescription("Edit round").performClick()
        composeTestRule.onNodeWithText("Edit round").assertIsDisplayed()
        composeTestRule.onNodeWithText("Save").performClick()
        composeTestRule.waitUntil { saved != null }
        assertEquals(sampleRound.roundId, saved!!.first)
        assertEquals(Leader.RShanks, saved!!.second.leader)
    }

    @Test
    fun editRound_delete_removesRound() {
        var removed: Int? = null
        setSheetContent(rounds = listOf(sampleRound), removeRound = { removed = it })
        composeTestRule.onNodeWithContentDescription("Edit round").performClick()
        composeTestRule.onNodeWithText("Delete").performClick()
        composeTestRule.waitUntil { removed != null }
        assertEquals(sampleRound.roundId, removed)
    }

    // endregion

    // region — Leader search

    private val searchLeaders = listOf(Leader.UGLuffy, Leader.GYRobin, Leader.RShanks, Leader.PYRobin)

    private fun setLeaderSelection(onLeaderSelected: (Leader) -> Unit = {}) {
        composeTestRule.setContent {
            LabophaseTheme {
                LeaderSelection(leaders = searchLeaders, onLeaderSelected = onLeaderSelected)
            }
        }
    }

    @Test
    fun leaderSearch_filtersCarouselByName() {
        setLeaderSelection()
        composeTestRule.onNodeWithText("Search leaders").performTextInput("robin")
        composeTestRule.onNodeWithContentDescription("GY Robin").assertExists()
        composeTestRule.onNodeWithContentDescription("UG Luffy").assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription("R Shanks").assertDoesNotExist()
    }

    @Test
    fun leaderSearch_noMatches_showsMessage() {
        setLeaderSelection()
        composeTestRule.onNodeWithText("Search leaders").performTextInput("zoro")
        composeTestRule.onNodeWithText("No leaders match").assertIsDisplayed()
    }

    @Test
    fun leaderSearch_clearButton_restoresFullList() {
        setLeaderSelection()
        composeTestRule.onNodeWithText("Search leaders").performTextInput("shanks")
        composeTestRule.onNodeWithContentDescription("UG Luffy").assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription("Clear search").performClick()
        composeTestRule.onNodeWithContentDescription("UG Luffy").assertExists()
    }

    @Test
    fun leaderSearch_tappingResult_selectsLeader() {
        var selected: Leader? = null
        setLeaderSelection { selected = it }
        composeTestRule.onNodeWithText("Search leaders").performTextInput("shanks")
        composeTestRule.onNodeWithContentDescription("R Shanks").performClick()
        assertEquals(Leader.RShanks, selected)
    }

    // endregion
}
