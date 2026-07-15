package commanderpepper.labophase.screens.roundentry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import commanderpepper.labophase.data.EntryRepository
import commanderpepper.labophase.logic.LeaderOrderDecider
import commanderpepper.labophase.models.Leader
import commanderpepper.labophase.models.Round
import commanderpepper.labophase.models.RoundResult
import commanderpepper.labophase.models.TurnOrder
import commanderpepper.labophase.models.leaderByCardId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class RoundEntryViewModelImpl(
    private val leaderOrderDecider: LeaderOrderDecider,
    private val entryRepository: EntryRepository,
    private val entryId: Int = -1
) : RoundEntryViewModel, ViewModel() {

    private var roundId: Int = 0

    private val _leaderSelected: MutableStateFlow<Leader> = MutableStateFlow(Leader.UGLuffy)
    override val leaderSelected: StateFlow<Leader> = _leaderSelected

    private val _rounds = MutableStateFlow<Map<Int, Round>>(emptyMap())
    override val rounds: StateFlow<List<Round>> = _rounds
        .map { it.values.toList() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _playerLeaderList: MutableStateFlow<List<Leader>> = MutableStateFlow(emptyList())
    override val playerLeaderList: StateFlow<List<Leader>> = _playerLeaderList.asStateFlow()

    private val _roundLeaderList: MutableStateFlow<List<Leader>> = MutableStateFlow(emptyList())
    override val roundLeaderList: StateFlow<List<Leader>> = _roundLeaderList

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (entryId != -1) {
                    val existing = entryRepository.getEntryById(entryId)
                    if (existing != null) {
                        _leaderSelected.value = leaderByCardId(existing.entry.leaderCardId)
                        val loaded = existing.rounds.mapIndexed { index, r ->
                            Round(
                                roundId = index,
                                roundNumber = r.roundNumber,
                                leader = leaderByCardId(r.leaderCardId),
                                roundResult = if (r.roundResult == "Win") RoundResult.Win else RoundResult.Loss,
                                turnOrder = if (r.turnOrder == "First") TurnOrder.First else TurnOrder.Second
                            )
                        }
                        _rounds.value = loaded.associateBy { it.roundId }
                        roundId = loaded.size
                    }
                }
                _playerLeaderList.value = leaderOrderDecider.getPlayerLeaderSelect()
                _roundLeaderList.value = leaderOrderDecider.getRoundLeaderSelect()
            }
        }
    }

    private val _punkRecordEntry: MutableStateFlow<String> = MutableStateFlow("")
    override val punkRecordEntry: StateFlow<String> = _punkRecordEntry

    override fun addNewRound() {
        val round = Round(roundId = roundId++, roundNumber = 1)
        _rounds.value = _rounds.value + (round.roundId to round)
    }

    override fun transformEntry() {
        val leader = _leaderSelected.value
        val rounds = _rounds.value.values.toList()
        updatePunkRecord(leader, rounds)
        viewModelScope.launch { saveEntry(leader, rounds) }
    }

    private fun updatePunkRecord(leader: Leader, rounds: List<Round>) {
        val formatted = rounds.map { round ->
            "${if (round.roundResult == RoundResult.Win) "W" else "L"} ${round.leader.name} ${if (round.turnOrder == TurnOrder.First) "1st" else "2nd"}"
        }.joinToString(separator = "\n")
        _punkRecordEntry.value = "!PR add\n${leader.name}\n$formatted"
    }

    private suspend fun saveEntry(leader: Leader, rounds: List<Round>) {
        entryRepository.saveEntry(leader, rounds)
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