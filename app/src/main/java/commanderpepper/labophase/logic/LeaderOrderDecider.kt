package commanderpepper.labophase.logic

import commanderpepper.labophase.models.Leader

interface LeaderOrderDecider {
    fun getPlayerLeaderSelect(): List<Leader>
    fun getRoundLeaderSelect(): List<Leader>
}