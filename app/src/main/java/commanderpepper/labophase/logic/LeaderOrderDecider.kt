package commanderpepper.labophase.logic

import commanderpepper.labophase.models.Leader

interface LeaderOrderDecider {
    suspend fun getPlayerLeaderSelect(): List<Leader>
    suspend fun getRoundLeaderSelect(): List<Leader>
}