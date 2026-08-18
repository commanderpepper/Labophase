package commanderpepper.labophase.screens.entries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import commanderpepper.labophase.data.EntryRepository
import commanderpepper.labophase.data.OpponentRepository
import commanderpepper.labophase.logic.converter.EntryToEntrySelectionUIConverter
import commanderpepper.labophase.screens.entries.models.EntrySelectionUIState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EntrySelectionViewModelImpl(
    private val eventRepository: EntryRepository,
    private val entrySelectionUIConverter: EntryToEntrySelectionUIConverter,
    private val opponentRepository: OpponentRepository
) : EntrySelectionViewModel, ViewModel() {

    override val entrySelectionUiState: StateFlow<EntrySelectionUIState> =
        combine(
            eventRepository.getAllEntries(),
            opponentRepository.getOpponents()
        ) { list, opponents ->
            EntrySelectionUIState(
                entries = list.map { entrySelectionUIConverter.entryToEntrySelectionUI(it, opponents) },
                isLoading = false
            )
        }
        .catch { emit(EntrySelectionUIState(errorMessage = "Something went wrong", isLoading = false)) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            EntrySelectionUIState(isLoading = true)
        )

    override fun deleteEntry(entryId: Int) {
        viewModelScope.launch { eventRepository.deleteEntry(entryId) }
    }
}
