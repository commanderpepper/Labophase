package commanderpepper.labophase.screens.roundentry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import commanderpepper.labophase.models.LEADERS_LIST
import commanderpepper.labophase.models.Leader
import commanderpepper.labophase.models.Round
import commanderpepper.labophase.models.RoundResult
import commanderpepper.labophase.models.TurnOrder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlin.math.round

class RoundEntryViewModelImpl : RoundEntryViewModel, ViewModel() {

    private var roundId: Int = 0

    private val _leaderSelected: MutableStateFlow<Leader> = MutableStateFlow(Leader.PLuffy)
    override val leaderSelected: StateFlow<Leader> = _leaderSelected

    private val _rounds = MutableStateFlow<Map<Int, Round>>(emptyMap())
    override val rounds: StateFlow<List<Round>> = _rounds
        .map { it.values.toList() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _leaders: MutableStateFlow<List<Leader>> = MutableStateFlow(
        LEADERS_LIST
    )
    override val leaders: StateFlow<List<Leader>> = _leaders.asStateFlow()

    private val _punkRecordEntry: MutableStateFlow<String> = MutableStateFlow("")
    override val punkRecordEntry: StateFlow<String> = _punkRecordEntry

    override fun addNewRound() {
        val round = Round(roundId++)
        _rounds.value = _rounds.value + (round.roundId to round)
    }

    override fun transformEntry() {
        val rounds = _rounds.value.values.toList().map { round ->
            "${if (round.roundResult == RoundResult.Win) "W" else "L"} ${round.leader.name} ${if (round.turnOrder == TurnOrder.First) "1st" else "2nd"}"
        }
        val a = rounds.joinToString(separator = "\n")
        val z = "!PR add\n${leaderSelected.value.name}\n$a"
        val entry = """
            !PR add 
            ${leaderSelected.value.name}
            ${rounds.map { it + "\n" }}
        """.trimIndent()
        _punkRecordEntry.value = z
    }

    override fun chooseLeader(leader: Leader) {
        _leaderSelected.value = leader
    }

    private fun updateRound(roundId: Int, update: (Round) -> Round) {
        val roundToUpdate = _rounds.value[roundId] ?: return
        _rounds.value = _rounds.value + (roundId to update(roundToUpdate))
    }

    override fun roundLeaderSelect(roundId: Int, leader: Leader) {
        updateRound(roundId) { it.copy(leader = leader) }
    }

    override fun roundTurnOrderSelect(roundId: Int, turnOrder: TurnOrder) {
        updateRound(roundId) { it.copy(turnOrder = turnOrder) }
    }

    override fun roundResultSelect(roundId: Int, roundResult: RoundResult) {
        updateRound(roundId) { it.copy(roundResult = roundResult) }
    }

    override fun removeRound(roundId: Int) {
        _rounds.value = _rounds.value.minus(roundId)
    }
}