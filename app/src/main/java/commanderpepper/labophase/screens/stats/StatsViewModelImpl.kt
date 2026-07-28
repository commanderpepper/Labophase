package commanderpepper.labophase.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import commanderpepper.labophase.data.EntryRepository
import commanderpepper.labophase.data.EntryWithRounds
import commanderpepper.labophase.models.Leader
import commanderpepper.labophase.models.Location
import commanderpepper.labophase.models.Meta
import commanderpepper.labophase.models.RoundResult
import commanderpepper.labophase.models.TurnOrder
import commanderpepper.labophase.models.leaderByCardId
import commanderpepper.labophase.screens.stats.models.LeaderSelectedOption
import commanderpepper.labophase.screens.stats.models.LocationOption
import commanderpepper.labophase.screens.stats.models.MetaOption
import commanderpepper.labophase.screens.stats.models.StatLeaderInfo
import commanderpepper.labophase.screens.stats.models.StatsUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class StatsViewModelImpl(
    private val entryRepository: EntryRepository
) : StatsViewModel, ViewModel() {

    private val _selectedLeader = MutableStateFlow<Leader?>(null)
    private val _selectedMeta = MutableStateFlow<Meta?>(null)
    private val _selectedLocation = MutableStateFlow<Location?>(null)

    private val _statsUIState = MutableStateFlow(
        StatsUIState(
            leaderSelected = LeaderSelectedOption.All,
            metaSelected = MetaOption.All,
            locationSelected = LocationOption.All,
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
                    _selectedLeader,
                    _selectedMeta,
                    _selectedLocation
                ) { entries, leader, meta, location ->
                    computeStats(entries, leader, meta, location)
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
        selectedLeader: Leader?,
        selectedMeta: Meta?,
        selectedLocation: Location?
    ): StatsUIState {
        val leadersPlayed = entries
            .map { leaderByCardId(it.entry.leaderCardId) }
            .distinct()

        val filtered = entries.filter { entryWithRounds ->
            val entry = entryWithRounds.entry
            (selectedLeader == null || entry.leaderCardId == selectedLeader.cardId) &&
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

        val leaderOption = if (selectedLeader != null) {
            LeaderSelectedOption.LeaderSelected(
                StatLeaderInfo(
                    leader = selectedLeader,
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

        return StatsUIState(
            leaderSelected = leaderOption,
            metaSelected = if (selectedMeta != null) MetaOption.SpecificMeta(selectedMeta) else MetaOption.All,
            locationSelected = if (selectedLocation != null) LocationOption.SpecificLocation(selectedLocation) else LocationOption.All,
            leadersPlayed = leadersPlayed,
            leadersPlayerAgainst = leadersAgainst,
            isLoading = false
        )
    }

    private fun pct(wins: Int, losses: Int): String {
        val total = wins + losses
        return if (total > 0) "${wins * 100 / total}%" else "N/A"
    }

    override fun allLeadersSelected() { _selectedLeader.value = null }
    override fun leaderSelected(leader: Leader) { _selectedLeader.value = leader }
    override fun metaSelected(meta: Meta) { _selectedMeta.value = meta }
    override fun allMetaSelected() { _selectedMeta.value = null }
    override fun locationSelected(location: Location) { _selectedLocation.value = location }
    override fun allLocationSelected() { _selectedLocation.value = null }
}
