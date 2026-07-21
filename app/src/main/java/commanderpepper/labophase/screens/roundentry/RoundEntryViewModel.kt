package commanderpepper.labophase.screens.roundentry

import commanderpepper.labophase.models.Leader
import commanderpepper.labophase.screens.roundentry.models.RoundEntryUIState
import kotlinx.coroutines.flow.StateFlow

interface RoundEntryViewModel {

    val uiState: StateFlow<RoundEntryUIState>

    fun addNewRound()

    fun transformEntry()

    fun chooseLeader(leader: Leader)

    fun roundLeaderSelect(roundId: Int, leader: Leader)

    fun roundTurnOrderSelect(roundId: Int, turnOrder: String)

    fun roundResultSelect(roundId: Int, roundResult: String)

    fun roundDieRollSelect(roundId: Int, dieRoll: String?)

    fun removeRound(roundId: Int)
}