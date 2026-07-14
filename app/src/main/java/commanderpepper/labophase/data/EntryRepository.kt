package commanderpepper.labophase.data

import commanderpepper.labophase.models.Leader
import commanderpepper.labophase.models.Round
import commanderpepper.labophase.models.RoundResult
import commanderpepper.labophase.models.TurnOrder
import kotlinx.coroutines.flow.Flow

interface EntryRepository {
    suspend fun saveEntry(leader: Leader, rounds: List<Round>)
    fun getAllEntries(): Flow<List<EntryWithRounds>>
    suspend fun getEntries(): List<EntryWithRounds>
}

class EntryRepositoryImpl(private val dao: EntryDao) : EntryRepository {
    override suspend fun saveEntry(leader: Leader, rounds: List<Round>) {
        val entryId = dao.insertEntry(EntryEntity(leaderCardId = leader.cardId)).toInt()
        dao.insertRounds(rounds.map { r ->
            RoundEntity(
                entryId = entryId,
                roundNumber = r.roundNumber,
                leaderCardId = r.leader.cardId,
                roundResult = if (r.roundResult is RoundResult.Win) "Win" else "Loss",
                turnOrder = if (r.turnOrder is TurnOrder.First) "First" else "Second"
            )
        })
    }

    override fun getAllEntries(): Flow<List<EntryWithRounds>> = dao.getAllEntries()

    override suspend fun getEntries(): List<EntryWithRounds> {
        return dao.getEntries()
    }
}
