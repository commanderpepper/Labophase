package commanderpepper.labophase.screens.stats

import commanderpepper.labophase.models.Leader
import commanderpepper.labophase.models.Location
import commanderpepper.labophase.models.Meta
import commanderpepper.labophase.screens.stats.models.StatsUIState
import kotlinx.coroutines.flow.StateFlow

interface StatsViewModel {
    val statsUIState: StateFlow<StatsUIState>

    fun allLeadersSelected()

    fun leaderSelected(leader: Leader)

    fun metaSelected(meta: Meta)

    fun allMetaSelected()

    fun locationSelected(location: Location)

    fun allLocationSelected()
}