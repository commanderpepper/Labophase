package commanderpepper.labophase.screens.roundentry

import commanderpepper.labophase.models.Entry
import commanderpepper.labophase.models.Leader
import commanderpepper.labophase.models.Round
import commanderpepper.labophase.models.RoundResult
import commanderpepper.labophase.models.TurnOrder
import kotlinx.coroutines.flow.StateFlow

interface RoundEntryViewModel {

    val leaderSelected: StateFlow<Leader>

    val rounds: StateFlow<List<Round>>

    val leaders: StateFlow<List<Leader>>

    val punkRecordEntry: StateFlow<String>

    fun addNewRound()

    fun transformEntry()

    fun chooseLeader(leader: Leader)

    fun roundLeaderSelect(roundId: Int, leader: Leader)

    fun roundTurnOrderSelect(roundId: Int, turnOrder: TurnOrder)

    fun roundResultSelect(roundId: Int, roundResult: RoundResult)

    fun removeRound(roundId: Int)
}