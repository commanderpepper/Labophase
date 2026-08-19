package commanderpepper.labophase.logic

import commanderpepper.labophase.models.Leader

interface LeaderOrderDecider {
    suspend fun getPlayerLeaderSelect(metaId: Int? = null): List<Leader>
    suspend fun getRoundLeaderSelect(metaId: Int? = null): List<Leader>
}