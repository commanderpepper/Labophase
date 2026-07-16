package commanderpepper.labophase.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

interface SettingsRepository {

     fun showingAllLeaders(): Flow<Boolean>

     fun showingDieRoll(): Flow<Boolean>

    suspend fun toggleDieRoll()

    suspend fun toggleLeadersShown()

    suspend fun clearHistory()

}

class SettingsRepositoryImpl(private val dataStore: DataStore<Preferences>, private val entryRepository: EntryRepository) : SettingsRepository {

    private val SHOW_ALL_LEADERS = booleanPreferencesKey("show_all_leaders")
    private val SHOW_DIE_ROLL = booleanPreferencesKey("show_die_rolls")

    override fun showingAllLeaders(): Flow<Boolean> {
        return dataStore.data.map { it[SHOW_ALL_LEADERS] ?: false }
    }

    override fun showingDieRoll(): Flow<Boolean> {
        return dataStore.data.map { it[SHOW_DIE_ROLL] ?: false }
    }

    override suspend fun toggleDieRoll() {
        dataStore.edit { prefs ->
            prefs[SHOW_DIE_ROLL] = !(prefs[SHOW_DIE_ROLL] ?: false)
        }
    }

    override suspend fun toggleLeadersShown() {
        dataStore.edit { prefs ->
            prefs[SHOW_ALL_LEADERS] = !(prefs[SHOW_ALL_LEADERS] ?: false)
        }
    }

    override suspend fun clearHistory() {
        entryRepository.deleteAllEntries()
    }
}
