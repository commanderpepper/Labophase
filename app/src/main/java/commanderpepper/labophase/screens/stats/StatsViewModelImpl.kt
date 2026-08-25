package commanderpepper.labophase.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import commanderpepper.labophase.data.EntryRepository
import commanderpepper.labophase.data.EntryWithRounds
import commanderpepper.labophase.data.OpponentRepository
import commanderpepper.labophase.models.Leader
import commanderpepper.labophase.models.Location
import commanderpepper.labophase.models.Meta
import commanderpepper.labophase.models.Opponent
import commanderpepper.labophase.models.RoundResult
import commanderpepper.labophase.models.TurnOrder
import commanderpepper.labophase.models.findById
import commanderpepper.labophase.models.leaderByCardId
import commanderpepper.labophase.models.locationById
import commanderpepper.labophase.screens.stats.models.LeaderSelectedOption
import commanderpepper.labophase.screens.stats.models.LocationOption
import commanderpepper.labophase.screens.stats.models.MetaOption
import commanderpepper.labophase.screens.stats.models.StatLeaderInfo
import commanderpepper.labophase.screens.stats.models.StatOpponentInfo
import commanderpepper.labophase.screens.stats.models.StatsTab
import commanderpepper.labophase.screens.stats.models.StatsUIState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class StatsViewModelImpl(
    private val entryRepository: EntryRepository,
    private val opponentRepository: OpponentRepository,
    private val computationDispatcher: CoroutineDispatcher = Dispatchers.Default
) : StatsViewModel, ViewModel() {

    private val TAG = "StatsViewModelImpl"

    private val _selectedLeader = MutableStateFlow<Leader?>(null)
    private val _selectedMeta = MutableStateFlow<Meta?>(null)
    private val _selectedLocation = MutableStateFlow<Location?>(null)

    private val _statsUIState = MutableStateFlow(
        StatsUIState(
            leaderSelected = LeaderSelectedOption.All,
            metaSelected = MetaOption.All,
            locationSelected = LocationOption.All,
            availableLocations = emptyList(),
            leadersPlayed = emptyList(),
            leadersPlayerAgainst = emptyList(),
            isLoading = true
        )
    )
    override val statsUIState: StateFlow<StatsUIState> = _statsUIState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                combine(
                    entryRepository.getAllEntries(),
                    opponentRepository.getOpponents(),
                    _selectedLeader,
                    _selectedMeta,
                    _selectedLocation
                ) { entries, opponents, leader, meta, location ->
                    withContext(computationDispatcher) { computeStats(entries, opponents, leader, meta, location) }
                }.collect { newState ->
                    _statsUIState.value = newState
                }
            } catch (e: Exception) {
                _statsUIState.value = _statsUIState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "An unexpected error occurred"
                )
            }
        }
    }

    private fun computeStats(
        entries: List<EntryWithRounds>,
        opponents: List<Opponent>,
        selectedLeader: Leader?,
        selectedMeta: Meta?,
        selectedLocation: Location?
    ): StatsUIState {
        val leadersPlayed = entries
            .filter { selectedMeta == null || it.entry.metaId == selectedMeta.id }
            .map { leaderByCardId(it.entry.leaderCardId) }
            .distinct()

        val availableLocations = entries
            .filter { selectedMeta == null || it.entry.metaId == selectedMeta.id }
            .mapNotNull { locationById(it.entry.locationId) }
            .distinct()
            .sortedBy { it.id }

        val effectiveLeader = if (selectedLeader != null && selectedLeader in leadersPlayed) selectedLeader else null

        val filtered = entries.filter { entryWithRounds ->
            val entry = entryWithRounds.entry
            (effectiveLeader == null || entry.leaderCardId == effectiveLeader.cardId) &&
            (selectedMeta == null || entry.metaId == selectedMeta.id) &&
            (selectedLocation == null || entry.locationId == selectedLocation.id)
        }

        val allRounds = filtered.flatMap { it.rounds }
        val wins = allRounds.count { it.roundResult == RoundResult.Win }
        val losses = allRounds.count { it.roundResult == RoundResult.Loss }
        val firstRounds = allRounds.filter { it.turnOrder == TurnOrder.First }
        val secondRounds = allRounds.filter { it.turnOrder == TurnOrder.Second }
        val firstWins = firstRounds.count { it.roundResult == RoundResult.Win }
        val firstLosses = firstRounds.count { it.roundResult == RoundResult.Loss }
        val secondWins = secondRounds.count { it.roundResult == RoundResult.Win }
        val secondLosses = secondRounds.count { it.roundResult == RoundResult.Loss }

        val leadersAgainst = allRounds
            .groupBy { leaderByCardId(it.leaderCardId) }
            .map { (leader, rounds) ->
                val w = rounds.count { it.roundResult == RoundResult.Win }
                val l = rounds.count { it.roundResult == RoundResult.Loss }
                val fr = rounds.filter { it.turnOrder == TurnOrder.First }
                val sr = rounds.filter { it.turnOrder == TurnOrder.Second }
                val fw = fr.count { it.roundResult == RoundResult.Win }
                val fl = fr.count { it.roundResult == RoundResult.Loss }
                val sw = sr.count { it.roundResult == RoundResult.Win }
                val sl = sr.count { it.roundResult == RoundResult.Loss }
                StatLeaderInfo(
                    leader = leader,
                    wins = w,
                    losses = l,
                    percentage = pct(w, l),
                    firstWins = fw,
                    firstLosses = fl,
                    firstPercentage = pct(fw, fl),
                    secondWins = sw,
                    secondLosses = sl,
                    secondPercentage = pct(sw, sl)
                )
            }
            .sortedByDescending { it.wins + it.losses }

        val roundsByOpponentId = allRounds
            .filter { it.opponentId != null }
            .groupBy { it.opponentId!! }
        val opponentStats = opponents
            .map { opp ->
                val rounds = roundsByOpponentId[opp.id] ?: emptyList()
                val w = rounds.count { it.roundResult == RoundResult.Win }
                val l = rounds.count { it.roundResult == RoundResult.Loss }
                StatOpponentInfo(opponent = opp, wins = w, losses = l, percentage = pct(w, l))
            }
            .sortedByDescending { it.wins + it.losses }

        val leaderOption = if (effectiveLeader != null) {
            LeaderSelectedOption.LeaderSelected(
                StatLeaderInfo(
                    leader = effectiveLeader,
                    wins = wins,
                    losses = losses,
                    percentage = pct(wins, losses),
                    firstWins = firstWins,
                    firstLosses = firstLosses,
                    firstPercentage = pct(firstWins, firstLosses),
                    secondWins = secondWins,
                    secondLosses = secondLosses,
                    secondPercentage = pct(secondWins, secondLosses)
                )
            )
        } else {
            LeaderSelectedOption.All
        }

        val effectiveLocation = if (selectedLocation != null && selectedLocation in availableLocations) selectedLocation else null

        return StatsUIState(
            leaderSelected = leaderOption,
            metaSelected = if (selectedMeta != null) MetaOption.SpecificMeta(selectedMeta) else MetaOption.All,
            locationSelected = if (effectiveLocation != null) LocationOption.SpecificLocation(effectiveLocation) else LocationOption.All,
            availableLocations = availableLocations,
            leadersPlayed = leadersPlayed,
            leadersPlayerAgainst = leadersAgainst,
            opponentStats = opponentStats,
            activeTab = _statsUIState.value.activeTab,
            isLoading = false
        )
    }

    private fun pct(wins: Int, losses: Int): Int? {
        val total = wins + losses
        return if (total > 0) wins * 100 / total else null
    }

    override fun allLeadersSelected() { _selectedLeader.value = null }
    override fun leaderSelected(leader: Leader) { _selectedLeader.value = leader }
    override fun metaSelected(meta: Meta) { _selectedLocation.value = null; _selectedMeta.value = meta }
    override fun allMetaSelected() { _selectedLocation.value = null; _selectedMeta.value = null }
    override fun locationSelected(location: Location) { _selectedLocation.value = location }
    override fun allLocationSelected() { _selectedLocation.value = null }
    override fun tabSelected(tab: StatsTab) { _statsUIState.update { it.copy(activeTab = tab) } }
    override fun addOpponent(name: String, nickname: String) {
        viewModelScope.launch {
            try {
                opponentRepository.addOpponent(name, nickname)
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Failed to add opponent")
                _statsUIState.update { it.copy(errorMessage = "Failed to add opponent") }
            }
        }
    }

    override fun updateOpponent(id: Int, name: String, nickname: String) {
        viewModelScope.launch {
            try {
                opponentRepository.updateOpponent(id, name, nickname)
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Failed to update opponent $id")
                _statsUIState.update { it.copy(errorMessage = "Failed to update opponent") }
            }
        }
    }

    override fun deleteOpponent(id: Int) {
        viewModelScope.launch {
            try {
                opponentRepository.deleteOpponent(id)
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Failed to delete opponent $id")
                _statsUIState.update { it.copy(errorMessage = "Failed to delete opponent") }
            }
        }
    }
}
