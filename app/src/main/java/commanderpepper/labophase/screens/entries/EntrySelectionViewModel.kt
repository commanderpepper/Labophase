package commanderpepper.labophase.screens.entries

import commanderpepper.labophase.screens.entries.models.EntrySelectionUIState
import kotlinx.coroutines.flow.StateFlow

interface EntrySelectionViewModel {
    val entrySelectionUiState: StateFlow<EntrySelectionUIState>
    fun deleteEntry(entryId: Int)
}