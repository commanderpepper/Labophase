package commanderpepper.labophase.screens.entries

import commanderpepper.labophase.screens.entries.models.EntrySelectionUI
import kotlinx.coroutines.flow.StateFlow

interface EntrySelectionViewModel {
    val entries: StateFlow<List<EntrySelectionUI>>
}