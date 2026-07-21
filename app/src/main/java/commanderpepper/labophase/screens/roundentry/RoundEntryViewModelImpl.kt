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
import commanderpepper.labophase.screens.roundentry.models.RoundEntryUIState
import commanderpepper.labophase.screens.roundentry.models.RoundUI
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RoundEntryViewModelImpl(
    private val leaderOrderDecider: LeaderOrderDecider,
    private val entryRepository: EntryRepository,
    private val entryId: Int = -1,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : RoundEntryViewModel, ViewModel() {

    private var roundId: Int = 0
    private var roundsMap: Map<Int, Round> = emptyMap()

    private val _uiState = MutableStateFlow(RoundEntryUIState(leaderSelected = Leader.UGLuffy, isLoading = true))
    override val uiState: StateFlow<RoundEntryUIState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            withContext(ioDispatcher) {
                try {
                    var loadedLeader: Leader = Leader.UGLuffy
                    if (entryId != -1) {
                        val existing = entryRepository.getEntryById(entryId)
                        if (existing != null) {
                            loadedLeader = leaderByCardId(existing.entry.leaderCardId)
                            val loaded = existing.rounds.mapIndexed { index, r ->
                                Round(
                                    roundId = index,
                                    roundNumber = r.roundNumber,
                                    leader = leaderByCardId(r.leaderCardId),
                                    roundResult = if (r.roundResult == "Win") RoundResult.Win else RoundResult.Loss,
                                    turnOrder = if (r.turnOrder == "First") TurnOrder.First else TurnOrder.Second,
                                    dieRoll = r.dieRoll
                                )
                            }
                            roundsMap = loaded.associateBy { it.roundId }
                            roundId = loaded.size
                        }
                    }
                    val playerList = leaderOrderDecider.getPlayerLeaderSelect()
                    val roundList = leaderOrderDecider.getRoundLeaderSelect()
                    _uiState.update {
                        it.copy(
                            leaderSelected = loadedLeader,
                            rounds = roundsMap.values.map { r -> r.toRoundUI() },
                            playerLeaderList = playerList,
                            roundLeaderList = roundList,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                } catch (e: Exception) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Something went wrong") }
                }
            }
        }
    }

    override fun addNewRound() {
        val round = Round(roundId = roundId++, roundNumber = 1)
        roundsMap = roundsMap + (round.roundId to round)
        _uiState.update { it.copy(rounds = roundsMap.values.map { r -> r.toRoundUI() }) }
    }

    override fun transformEntry() {
        val leader = _uiState.value.leaderSelected
        val rounds = roundsMap.values.toList()
        updatePunkRecord(leader, rounds)
        viewModelScope.launch { saveEntry(leader, rounds) }
    }

    private fun updatePunkRecord(leader: Leader, rounds: List<Round>) {
        val formatted = rounds.map { round ->
            "${if (round.roundResult == RoundResult.Win) "W" else "L"} ${round.leader.name} ${if (round.turnOrder == TurnOrder.First) "1st" else "2nd"}"
        }.joinToString(separator = "\n")
        _uiState.update { it.copy(punkRecordEntry = "!PR add\n${leader.name}\n$formatted") }
    }

    private suspend fun saveEntry(leader: Leader, rounds: List<Round>) {
        if (entryId == -1) {
            entryRepository.saveEntry(leader, rounds)
        } else {
            entryRepository.updateEntry(entryId, leader, rounds)
        }
    }

    override fun chooseLeader(leader: Leader) {
        _uiState.update { it.copy(leaderSelected = leader) }
    }

    private fun updateRound(roundId: Int, update: (Round) -> Round) {
        val roundToUpdate = roundsMap[roundId] ?: return
        roundsMap = roundsMap + (roundId to update(roundToUpdate))
        _uiState.update { it.copy(rounds = roundsMap.values.map { r -> r.toRoundUI() }) }
    }

    override fun roundLeaderSelect(roundId: Int, leader: Leader) {
        updateRound(roundId) { it.copy(leader = leader) }
    }

    override fun roundTurnOrderSelect(roundId: Int, turnOrder: String) {
        updateRound(roundId) { it.copy(turnOrder = if (turnOrder == "First") TurnOrder.First else TurnOrder.Second) }
    }

    override fun roundResultSelect(roundId: Int, roundResult: String) {
        updateRound(roundId) { it.copy(roundResult = if (roundResult == "Win") RoundResult.Win else RoundResult.Loss) }
    }

    override fun roundDieRollSelect(roundId: Int, dieRoll: String?) {
        updateRound(roundId) { it.copy(dieRoll = dieRoll) }
    }

    override fun removeRound(roundId: Int) {
        roundsMap = roundsMap.minus(roundId)
        _uiState.update { it.copy(rounds = roundsMap.values.map { r -> r.toRoundUI() }) }
    }

    private fun Round.toRoundUI() = RoundUI(
        roundId = roundId,
        leader = leader,
        summary = singleLine(),
        roundResult = if (roundResult == RoundResult.Win) "Win" else "Loss",
        turnOrder = if (turnOrder == TurnOrder.First) "First" else "Second",
        dieRoll = dieRoll
    )
}
