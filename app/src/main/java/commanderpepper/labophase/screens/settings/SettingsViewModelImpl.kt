package commanderpepper.labophase.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import commanderpepper.labophase.data.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModelImpl(private val settingsRepository: SettingsRepository) : SettingsViewModel,
    ViewModel() {
    override val showingAllLeaders: StateFlow<Boolean> = settingsRepository.showingAllLeaders()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = false
        )

    override val showingDieRoll: StateFlow<Boolean> = settingsRepository.showingDieRoll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = false
        )

    override fun toggleLeaders() {
        viewModelScope.launch {
            settingsRepository.toggleLeadersShown()
        }
    }

    override fun toggleDieRoll() {
        viewModelScope.launch {
            settingsRepository.toggleDieRoll()
        }
    }

    override fun deleteHistory() {
        viewModelScope.launch {
            settingsRepository.clearHistory()
        }
    }
}