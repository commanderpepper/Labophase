package commanderpepper.labophase.screens.roundentry.models

import commanderpepper.labophase.models.Leader
import commanderpepper.labophase.models.Location
import commanderpepper.labophase.models.LOCATIONS_LIST
import commanderpepper.labophase.models.DEFAULT_META
import commanderpepper.labophase.models.DEFAULT_PLAYER_LEADER
import commanderpepper.labophase.models.Opponent
import java.time.LocalDate

data class RoundEntryUIState(
    val leaderSelected: Leader = DEFAULT_PLAYER_LEADER,
    val rounds: List<RoundUI> = emptyList(),
    val playerLeaderList: List<Leader> = emptyList(),
    val roundLeaderList: List<Leader> = emptyList(),
    val locationList: List<Location> = LOCATIONS_LIST,
    val opponentList: List<Opponent> = emptyList(),
    val punkRecordEntry: String = "",
    val title: String? = null,
    val locationId: Int? = null,
    val metaId: Int = DEFAULT_META.id,
    val date: String = LocalDate.now().toString(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
