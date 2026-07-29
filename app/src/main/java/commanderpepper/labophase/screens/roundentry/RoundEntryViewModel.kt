package commanderpepper.labophase.screens.roundentry

import commanderpepper.labophase.models.Leader
import commanderpepper.labophase.models.RoundResult
import commanderpepper.labophase.models.TurnOrder
import commanderpepper.labophase.screens.roundentry.models.RoundEntryUIState
import kotlinx.coroutines.flow.StateFlow

interface RoundEntryViewModel {

    val uiState: StateFlow<RoundEntryUIState>

    fun addNewRound()

    fun transformEntry()

    fun chooseLeader(leader: Leader)

    fun roundLeaderSelect(roundId: Int, leader: Leader)

    fun roundTurnOrderSelect(roundId: Int, turnOrder: TurnOrder)

    fun roundResultSelect(roundId: Int, roundResult: RoundResult)

    fun roundDieRollSelect(roundId: Int, dieRoll: String?)

    fun removeRound(roundId: Int)

    fun setTitle(title: String?)

    fun setLocationId(locationId: Int?)

    fun setMetaId(metaId: Int)

    fun setDate(date: String)
}
