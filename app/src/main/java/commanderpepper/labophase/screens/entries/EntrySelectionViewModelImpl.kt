package commanderpepper.labophase.screens.entries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import commanderpepper.labophase.data.EntryRepository
import commanderpepper.labophase.logic.converter.EntryToEntrySelectionUIConverter
import commanderpepper.labophase.screens.entries.models.EntrySelectionUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EntrySelectionViewModelImpl(private val eventRepository: EntryRepository, private val entrySelectionUIConverter: EntryToEntrySelectionUIConverter) : EntrySelectionViewModel, ViewModel() {

    private val _entries: MutableStateFlow<List<EntrySelectionUI>> = MutableStateFlow(emptyList())
    override val entries: StateFlow<List<EntrySelectionUI>> = _entries

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val entries = eventRepository.getEntries()
                _entries.value = entries.map { e -> entrySelectionUIConverter.entryToEntrySelectionUI(e)}
            }
        }
    }
}