package commanderpepper.labophase.screens.settings

import kotlinx.coroutines.flow.StateFlow

interface SettingsViewModel {

    val showingAllLeaders: StateFlow<Boolean>

    val showingDieRoll: StateFlow<Boolean>

    fun toggleLeaders()

    fun toggleDieRoll()

    fun deleteHistory()
}