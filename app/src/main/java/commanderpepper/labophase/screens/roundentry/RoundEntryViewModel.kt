package commanderpepper.labophase.screens.roundentry

import commanderpepper.labophase.models.Leader
import commanderpepper.labophase.screens.roundentry.models.RoundUI
import kotlinx.coroutines.flow.StateFlow

interface RoundEntryViewModel {

    val leaderSelected: StateFlow<Leader>

    val rounds: StateFlow<List<RoundUI>>

    val playerLeaderList: StateFlow<List<Leader>>

    val roundLeaderList: StateFlow<List<Leader>>

    val punkRecordEntry: StateFlow<String>

    fun addNewRound()

    fun transformEntry()

    fun chooseLeader(leader: Leader)

    fun roundLeaderSelect(roundId: Int, leader: Leader)

    fun roundTurnOrderSelect(roundId: Int, turnOrder: String)

    fun roundResultSelect(roundId: Int, roundResult: String)

    fun roundDieRollSelect(roundId: Int, dieRoll: String?)

    fun removeRound(roundId: Int)
}