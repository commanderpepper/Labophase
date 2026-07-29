package commanderpepper.labophase.screens.stats

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import commanderpepper.labophase.models.Leader
import commanderpepper.labophase.models.Meta
import commanderpepper.labophase.screens.stats.models.LeaderSelectedOption
import commanderpepper.labophase.screens.stats.models.LocationOption
import commanderpepper.labophase.screens.stats.models.MetaOption
import commanderpepper.labophase.screens.stats.models.StatLeaderInfo
import commanderpepper.labophase.screens.stats.models.StatsUIState
import commanderpepper.labophase.ui.theme.LabophaseTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StatsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleLeaderInfo = StatLeaderInfo(
        leader = Leader.UGLuffy,
        wins = 3, losses = 1, percentage = 75,
        firstWins = 2, firstLosses = 0, firstPercentage = 100,
        secondWins = 1, secondLosses = 1, secondPercentage = 50
    )

    // Two opponents so the aggregate header values differ from individual row values
    private val opponentShanks = StatLeaderInfo(
        leader = Leader.RShanks,
        wins = 2, losses = 1, percentage = 66,
        firstWins = 2, firstLosses = 0, firstPercentage = 100,
        secondWins = 0, secondLosses = 1, secondPercentage = 0
    )
    private val opponentLuffy = StatLeaderInfo(
        leader = Leader.PBLuffy,
        wins = 1, losses = 2, percentage = 33,
        firstWins = 0, firstLosses = 1, firstPercentage = 0,
        secondWins = 1, secondLosses = 1, secondPercentage = 50
    )

    private fun setContent(
        statsUIState: StatsUIState,
        onLeaderSelected: (Leader) -> Unit = {},
        onAllLeadersSelected: () -> Unit = {},
        onMetaSelected: (Meta) -> Unit = {},
        onAllMetasSelected: () -> Unit = {},
        onLocationSelected: () -> Unit = {},
        onAllLocationsSelected: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            LabophaseTheme {
                StatsScreen(
                    statsUIState = statsUIState,
                    onLeaderSelected = onLeaderSelected,
                    onAllLeadersSelected = onAllLeadersSelected,
                    onMetaSelected = onMetaSelected,
                    onAllMetasSelected = onAllMetasSelected,
                    onLocationSelected = { onLocationSelected() },
                    onAllLocationsSelected = onAllLocationsSelected
                )
            }
        }
    }

    private fun loadingState() = StatsUIState(
        leaderSelected = LeaderSelectedOption.All,
        metaSelected = MetaOption.All,
        locationSelected = LocationOption.All,
        leadersPlayed = emptyList(),
        leadersPlayerAgainst = emptyList(),
        isLoading = true
    )

    private fun allLeadersState(
        leadersPlayed: List<Leader> = listOf(Leader.UGLuffy),
        opponents: List<StatLeaderInfo> = listOf(opponentShanks, opponentLuffy)
    ) = StatsUIState(
        leaderSelected = LeaderSelectedOption.All,
        metaSelected = MetaOption.All,
        locationSelected = LocationOption.All,
        leadersPlayed = leadersPlayed,
        leadersPlayerAgainst = opponents,
        isLoading = false
    )

    private fun leaderSelectedState(opponents: List<StatLeaderInfo> = emptyList()) = StatsUIState(
        leaderSelected = LeaderSelectedOption.LeaderSelected(sampleLeaderInfo),
        metaSelected = MetaOption.All,
        locationSelected = LocationOption.All,
        leadersPlayed = listOf(Leader.UGLuffy),
        leadersPlayerAgainst = opponents,
        isLoading = false
    )

    // region — Loading / Error

    @Test
    fun loadingState_showsProgressIndicator() {
        setContent(loadingState())
        composeTestRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertExists()
    }

    @Test
    fun errorState_showsErrorMessage() {
        setContent(
            allLeadersState().copy(errorMessage = "Something went wrong")
        )
        composeTestRule.onNodeWithText("Something went wrong").assertIsDisplayed()
    }

    // endregion

    // region — Top bar

    @Test
    fun statsTitle_isDisplayed() {
        setContent(allLeadersState())
        composeTestRule.onNodeWithText("Stats").assertIsDisplayed()
    }

    // endregion

    // region — Header

    @Test
    fun allLeadersState_showsAllLeadersLabel() {
        setContent(allLeadersState())
        composeTestRule.onAllNodesWithText("All Leaders")[0].assertIsDisplayed()
    }

    @Test
    fun leaderSelected_showsLeaderNameInHeader() {
        setContent(leaderSelectedState())
        composeTestRule.onNodeWithText(Leader.UGLuffy.name).assertIsDisplayed()
    }

    @Test
    fun header_showsOverallWinsAndLosses() {
        // No opponents so the header W/L is uniquely from the selected leader
        setContent(leaderSelectedState(opponents = emptyList()))
        composeTestRule.onNodeWithText("W: 3 - L: 1 (75%)").assertIsDisplayed()
    }

    @Test
    fun header_showsGoingFirstStats() {
        setContent(leaderSelectedState(opponents = emptyList()))
        composeTestRule.onNodeWithText("1st: W: 2 - L: 0 (100%)").assertIsDisplayed()
    }

    @Test
    fun header_showsGoingSecondStats() {
        setContent(leaderSelectedState(opponents = emptyList()))
        composeTestRule.onNodeWithText("2nd: W: 1 - L: 1 (50%)").assertIsDisplayed()
    }

    // endregion

    // region — Opponents list

    @Test
    fun leadersAgainst_sectionLabelIsDisplayed() {
        setContent(allLeadersState())
        composeTestRule.onNodeWithText("Leaders Played Against").assertIsDisplayed()
    }

    @Test
    fun opponentRow_showsOpponentName() {
        setContent(allLeadersState())
        composeTestRule.onNodeWithText(Leader.RShanks.name).assertIsDisplayed()
    }

    @Test
    fun opponentRow_showsWinsAndLosses() {
        // opponentShanks: 2W-1L (66%) — header aggregate is 3W-3L (50%), so this is unique
        setContent(allLeadersState())
        composeTestRule.onNodeWithText("W: 2 - L: 1 (66%)").assertIsDisplayed()
    }

    @Test
    fun opponentRow_showsGoingFirstStats() {
        // opponentShanks first: 2W-0L (100%) — header first aggregate is 2W-1L (66%), so unique
        setContent(allLeadersState())
        composeTestRule.onNodeWithText("1st: W: 2 - L: 0 (100%)").assertIsDisplayed()
    }

    @Test
    fun opponentRow_showsGoingSecondStats() {
        // opponentShanks second: 0W-1L (0%) — unique in the UI
        setContent(allLeadersState())
        composeTestRule.onNodeWithText("2nd: W: 0 - L: 1 (0%)").assertIsDisplayed()
    }

    // endregion

    // region — Filter dropdowns

    @Test
    fun metaDropdown_showsAllMetasWhenNoneSelected() {
        setContent(allLeadersState())
        composeTestRule.onNodeWithText("All Metas").assertIsDisplayed()
    }

    @Test
    fun metaDropdown_showsSelectedMetaName() {
        setContent(
            allLeadersState().copy(metaSelected = MetaOption.SpecificMeta(Meta.OP16))
        )
        composeTestRule.onNodeWithText(Meta.OP16.name).assertIsDisplayed()
    }

    @Test
    fun locationDropdown_showsAllLocationsWhenNoneSelected() {
        setContent(allLeadersState())
        composeTestRule.onNodeWithText("All Locations").assertIsDisplayed()
    }

    @Test
    fun metaDropdown_click_invokesOnMetaSelected() {
        var selectedMeta: Meta? = null
        composeTestRule.setContent {
            LabophaseTheme {
                StatsScreen(
                    statsUIState = allLeadersState(),
                    onLeaderSelected = {},
                    onAllLeadersSelected = {},
                    onMetaSelected = { selectedMeta = it },
                    onAllMetasSelected = {},
                    onLocationSelected = {},
                    onAllLocationsSelected = {}
                )
            }
        }
        composeTestRule.onNodeWithText("All Metas").performClick()
        composeTestRule.onAllNodesWithText(Meta.OP16.name)[0].performClick()
        assertEquals(Meta.OP16, selectedMeta)
    }

    // endregion
}
