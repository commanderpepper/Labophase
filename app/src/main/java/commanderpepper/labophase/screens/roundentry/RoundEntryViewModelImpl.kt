package commanderpepper.labophase.screens.roundentry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import commanderpepper.labophase.logic.LeaderOrderDecider
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

class RoundEntryViewModelImpl(private val leaderOrderDecider: LeaderOrderDecider) : RoundEntryViewModel, ViewModel() {

    private var roundId: Int = 0

    private val _leaderSelected: MutableStateFlow<Leader> = MutableStateFlow(Leader.UGLuffy)
    override val leaderSelected: StateFlow<Leader> = _leaderSelected

    private val _rounds = MutableStateFlow<Map<Int, Round>>(emptyMap())
    override val rounds: StateFlow<List<Round>> = _rounds
        .map { it.values.toList() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _playerLeaderList: MutableStateFlow<List<Leader>> = MutableStateFlow(
        leaderOrderDecider.getPlayerLeaderSelect()
    )
    override val playerLeaderList: StateFlow<List<Leader>> = _playerLeaderList.asStateFlow()

    private val _roundLeaderList: MutableStateFlow<List<Leader>> = MutableStateFlow(
        leaderOrderDecider.getRoundLeaderSelect()
    )
    override val roundLeaderList: StateFlow<List<Leader>> = _roundLeaderList

    private val _punkRecordEntry: MutableStateFlow<String> = MutableStateFlow("")
    override val punkRecordEntry: StateFlow<String> = _punkRecordEntry

    override fun addNewRound() {
        val round = Round(roundId = roundId++, roundNumber = 1)
        _rounds.value = _rounds.value + (round.roundId to round)
    }

    override fun transformEntry() {
        val rounds = _rounds.value.values.toList().map { round ->
            "${if (round.roundResult == RoundResult.Win) "W" else "L"} ${round.leader.name} ${if (round.turnOrder == TurnOrder.First) "1st" else "2nd"}"
        }.joinToString(separator = "\n")
        val entry = "!PR add\n${leaderSelected.value.name}\n$rounds"
        _punkRecordEntry.value = entry
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