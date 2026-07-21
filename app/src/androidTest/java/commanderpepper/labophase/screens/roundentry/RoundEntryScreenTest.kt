package commanderpepper.labophase.screens.roundentry

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import commanderpepper.labophase.models.Leader
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
        roundResult = "Win",
        turnOrder = "First"
    )

    private fun setIdleContent(
        rounds: List<RoundUI> = emptyList(),
        punkRecordEntry: String = ""
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

    @Test
    fun punkRecordHeader_visibleWhenPunkRecordEntryIsNonEmpty() {
        setIdleContent(punkRecordEntry = "!PR add\nUG Luffy\nW R Shanks 1st")
        composeTestRule.onNodeWithText("Punk Record").assertIsDisplayed()
    }
}
