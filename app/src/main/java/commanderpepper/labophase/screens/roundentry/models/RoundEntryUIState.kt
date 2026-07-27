package commanderpepper.labophase.screens.roundentry.models

import commanderpepper.labophase.models.Leader
import commanderpepper.labophase.models.Meta
import java.time.LocalDate

data class RoundEntryUIState(
    val leaderSelected: Leader = Leader.PBLuffy,
    val rounds: List<RoundUI> = emptyList(),
    val playerLeaderList: List<Leader> = emptyList(),
    val roundLeaderList: List<Leader> = emptyList(),
    val punkRecordEntry: String = "",
    val title: String? = null,
    val locationId: Int? = null,
    val metaId: Int = Meta.OP16.id,
    val date: String = LocalDate.now().toString(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
