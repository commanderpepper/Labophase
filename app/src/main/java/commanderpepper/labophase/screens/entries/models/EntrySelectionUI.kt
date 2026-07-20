package commanderpepper.labophase.screens.entries.models

import commanderpepper.labophase.models.Leader

data class EntrySelectionUI(
    val entryId: Int,
    val leader: Leader,
    val wins: Int,
    val losses: Int,
    val punkRecord: String,
    val rounds: List<RoundEntrySelectionUI>
)

data class RoundEntrySelectionUI(val leader: Leader, val summary: String)

data class EntrySelectionUIState (
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val entries: List<EntrySelectionUI> = emptyList()
)