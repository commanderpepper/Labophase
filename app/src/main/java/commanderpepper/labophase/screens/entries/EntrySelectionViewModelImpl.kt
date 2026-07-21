package commanderpepper.labophase.screens.entries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import commanderpepper.labophase.data.EntryRepository
import commanderpepper.labophase.logic.converter.EntryToEntrySelectionUIConverter
import commanderpepper.labophase.screens.entries.models.EntrySelectionUIState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EntrySelectionViewModelImpl(private val eventRepository: EntryRepository, private val entrySelectionUIConverter: EntryToEntrySelectionUIConverter) : EntrySelectionViewModel, ViewModel() {

    override val entrySelectionUiState: StateFlow<EntrySelectionUIState> = eventRepository
        .getAllEntries()
        .map { list -> EntrySelectionUIState(entries = list.map { entrySelectionUIConverter.entryToEntrySelectionUI(it) }, isLoading = false) }
        .catch { emit(EntrySelectionUIState(errorMessage = "Something went wrong", isLoading = false)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L),
            EntrySelectionUIState(isLoading = true)
        )

    override fun deleteEntry(entryId: Int) {
        viewModelScope.launch { eventRepository.deleteEntry(entryId) }
    }
}
