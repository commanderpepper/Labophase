package commanderpepper.labophase.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import commanderpepper.labophase.data.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

class SettingsViewModelImpl(private val settingsRepository: SettingsRepository) : SettingsViewModel,
    ViewModel() {

    private val TAG = "SettingsViewModelImpl"
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
            try {
                settingsRepository.toggleLeadersShown()
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Failed to toggle leaders setting")
            }
        }
    }

    override fun toggleDieRoll() {
        viewModelScope.launch {
            try {
                settingsRepository.toggleDieRoll()
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Failed to toggle die roll setting")
            }
        }
    }

    override fun deleteHistory() {
        viewModelScope.launch {
            try {
                settingsRepository.clearHistory()
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Failed to clear history")
            }
        }
    }
}