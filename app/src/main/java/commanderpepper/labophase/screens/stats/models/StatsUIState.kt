package commanderpepper.labophase.screens.stats.models

import commanderpepper.labophase.models.Leader
import commanderpepper.labophase.models.Location
import commanderpepper.labophase.models.Meta

data class StatsUIState(
    val leaderSelected: LeaderSelectedOption,
    val metaSelected: MetaOption,
    val locationSelected: LocationOption,
    val leadersPlayed: List<Leader>,
    val leadersPlayerAgainst: List<StatLeaderInfo>,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class MetaOption {
    object All: MetaOption()
    data class SpecificMeta(val meta: Meta): MetaOption()
}

sealed class LocationOption {
    object All: LocationOption()
    data class SpecificLocation(val location: Location): LocationOption()
}

sealed class LeaderSelectedOption {
    object All: LeaderSelectedOption()
    data class LeaderSelected(val leader: StatLeaderInfo): LeaderSelectedOption()
}

data class StatLeaderInfo(
    val leader: Leader,
    val wins: Int,
    val losses: Int,
    val percentage: Int?,
    val firstWins: Int = 0,
    val firstLosses: Int = 0,
    val firstPercentage: Int? = null,
    val secondWins: Int = 0,
    val secondLosses: Int = 0,
    val secondPercentage: Int? = null
)