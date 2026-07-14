package commanderpepper.labophase.logic

import commanderpepper.labophase.data.EntryRepository
import commanderpepper.labophase.models.LEADERS_LIST
import commanderpepper.labophase.models.Leader

class LeaderOrderDeciderImpl(private val entryRepository: EntryRepository) : LeaderOrderDecider {
    override suspend fun getPlayerLeaderSelect(): List<Leader> {
        val countByCardId = entryRepository.getEntries()
            .map { it.entry.leaderCardId }
            .groupingBy { it }
            .eachCount()
        return sortedByCount(countByCardId)
    }

    override suspend fun getRoundLeaderSelect(): List<Leader> {
        val countByCardId = entryRepository.getEntries()
            .flatMap { it.rounds }
            .map { it.leaderCardId }
            .groupingBy { it }
            .eachCount()
        return sortedByCount(countByCardId)
    }

    private fun sortedByCount(countByCardId: Map<String, Int>): List<Leader> =
        LEADERS_LIST.sortedWith(
            compareByDescending<Leader> { countByCardId[it.cardId] ?: 0 }
                .thenByDescending { it.set.number }
        )
}
