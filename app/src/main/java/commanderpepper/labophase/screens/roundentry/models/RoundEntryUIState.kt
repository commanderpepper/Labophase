package commanderpepper.labophase.screens.roundentry.models

import commanderpepper.labophase.models.Leader

data class RoundEntryUIState(
    val leaderSelected: Leader = Leader.PBLuffy,
    val rounds: List<RoundUI> = emptyList(),
    val playerLeaderList: List<Leader> = emptyList(),
    val roundLeaderList: List<Leader> = emptyList(),
    val punkRecordEntry: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
