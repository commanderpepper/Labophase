package commanderpepper.labophase.logic

import commanderpepper.labophase.models.LEADERS_LIST
import commanderpepper.labophase.models.Leader

class LeaderOrderDeciderImpl : LeaderOrderDecider {
    override fun getPlayerLeaderSelect(): List<Leader> {
        return LEADERS_LIST.sortedByDescending { leader -> leader.set.number }
    }

    override fun getRoundLeaderSelect(): List<Leader> {
        return LEADERS_LIST.sortedByDescending { leader -> leader.set.number }
    }
}