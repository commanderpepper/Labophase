package commanderpepper.labophase.screens.entries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import commanderpepper.labophase.data.EntryRepository
import commanderpepper.labophase.logic.converter.EntryToEntrySelectionUIConverter
import commanderpepper.labophase.screens.entries.models.EntrySelectionUI
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class EntrySelectionViewModelImpl(private val eventRepository: EntryRepository, private val entrySelectionUIConverter: EntryToEntrySelectionUIConverter) : EntrySelectionViewModel, ViewModel() {

    override val entries: StateFlow<List<EntrySelectionUI>> = eventRepository
        .getAllEntries()
        .map { list -> list.map { entrySelectionUIConverter.entryToEntrySelectionUI(it) } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
}
