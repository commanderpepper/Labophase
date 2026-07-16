package commanderpepper.labophase.logic

import commanderpepper.labophase.data.EntryRepository
import commanderpepper.labophase.data.SettingsRepository
import commanderpepper.labophase.models.LEADERS_LIST
import commanderpepper.labophase.models.Leader

class LeaderOrderDeciderImpl(private val entryRepository: EntryRepository, private val settingsRepository: SettingsRepository) : LeaderOrderDecider {
    override suspend fun getPlayerLeaderSelect(): List<Leader> {
        val leaders = filteredLeaders()
        val countByCardId = entryRepository.getEntries()
            .map { it.entry.leaderCardId }
            .groupingBy { it }
            .eachCount()
        return sortedByCount(leaders, countByCardId)
    }

    override suspend fun getRoundLeaderSelect(): List<Leader> {
        val leaders = filteredLeaders()
        val countByCardId = entryRepository.getEntries()
            .flatMap { it.rounds }
            .map { it.leaderCardId }
            .groupingBy { it }
            .eachCount()
        return sortedByCount(leaders, countByCardId)
    }

    private suspend fun filteredLeaders(): List<Leader> {
        val showAll = settingsRepository.isShowingAllLeaders()
        return if (showAll) LEADERS_LIST else LEADERS_LIST.filter { it.blockNumber != 1 }
    }

    private fun sortedByCount(leaders: List<Leader>, countByCardId: Map<String, Int>): List<Leader> =
        leaders.sortedWith(
            compareByDescending<Leader> { countByCardId[it.cardId] ?: 0 }
                .thenByDescending { it.set.number }
        )
}
