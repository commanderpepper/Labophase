package commanderpepper.labophase.screens.roundentry

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import commanderpepper.labophase.models.Leader
import commanderpepper.labophase.models.Location
import commanderpepper.labophase.models.RoundResult
import commanderpepper.labophase.models.TurnOrder
import commanderpepper.labophase.screens.roundentry.models.RoundUI
import commanderpepper.labophase.ui.theme.LabophaseTheme
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
                    addNewRound = {},
                    transformEntry = {},
                    chooseLeader = {},
                    chooseRoundLeader = { _, _ -> },
                    chooseRoundTurnOrder = { _, _ -> },
                    chooseRoundResult = { _, _ -> },
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
                    addNewRound = {},
                    transformEntry = {},
                    chooseLeader = {},
                    chooseRoundLeader = { _, _ -> },
                    chooseRoundTurnOrder = { _, _ -> },
                    chooseRoundResult = { _, _ -> },
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
                    addNewRound = {},
                    transformEntry = {},
                    chooseLeader = {},
                    chooseRoundLeader = { _, _ -> },
                    chooseRoundTurnOrder = { _, _ -> },
                    chooseRoundResult = { _, _ -> },
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

    @Test
    fun rowLabel_noTitle_showsLeaderNameInHeadline() {
        setIdleContent()
        composeTestRule.onNodeWithText("UG Luffy").assertIsDisplayed()
        composeTestRule.onNodeWithText("on Jul 30, 2026").assertIsDisplayed()
        composeTestRule.onNodeWithText("W: 0 - L: 0").assertIsDisplayed()
    }

    @Test
    fun rowLabel_withTitle_showsTitleInHeadline() {
        setIdleContent(title = "Seattle Regional")
        composeTestRule.onNodeWithText("Seattle Regional").assertIsDisplayed()
        composeTestRule.onNodeWithText("on Jul 30, 2026").assertIsDisplayed()
        composeTestRule.onNodeWithText("W: 0 - L: 0").assertIsDisplayed()
    }

    @Test
    fun rowLabel_withLocation_showsLocationInOverline() {
        setIdleContent(locationId = Location.ChronosGamesAndGifts.id)
        composeTestRule.onNodeWithText("UG Luffy").assertIsDisplayed()
        composeTestRule.onNodeWithText("Chronos on Jul 30, 2026").assertIsDisplayed()
        composeTestRule.onNodeWithText("W: 0 - L: 0").assertIsDisplayed()
    }

    @Test
    fun rowLabel_withTitleAndLocation_showsTitleInHeadlineAndLocationInOverline() {
        setIdleContent(title = "Seattle Regional", locationId = Location.ChronosGamesAndGifts.id)
        composeTestRule.onNodeWithText("Seattle Regional").assertIsDisplayed()
        composeTestRule.onNodeWithText("Chronos on Jul 30, 2026").assertIsDisplayed()
        composeTestRule.onNodeWithText("W: 0 - L: 0").assertIsDisplayed()
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
}
