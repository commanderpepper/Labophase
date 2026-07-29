package commanderpepper.labophase.data

import commanderpepper.labophase.data.entity.EntryEntity
import commanderpepper.labophase.data.entity.RoundEntity
import commanderpepper.labophase.models.Leader
import commanderpepper.labophase.models.Round
import kotlinx.coroutines.flow.Flow

interface EntryRepository {
    suspend fun saveEntry(
        leader: Leader,
        rounds: List<Round>,
        title: String? = null,
        locationId: Int? = null,
        metaId: Int,
        date: String
    ): Int
    suspend fun updateEntry(
        entryId: Int,
        leader: Leader,
        rounds: List<Round>,
        title: String? = null,
        locationId: Int? = null,
        metaId: Int,
        date: String
    )
    fun getAllEntries(): Flow<List<EntryWithRounds>>
    suspend fun getEntries(): List<EntryWithRounds>
    suspend fun getEntryById(id: Int): EntryWithRounds?
    suspend fun deleteEntry(entryId: Int)
    suspend fun deleteAllEntries()
}

class EntryRepositoryImpl(private val dao: EntryDao) : EntryRepository {
    override suspend fun saveEntry(
        leader: Leader,
        rounds: List<Round>,
        title: String?,
        locationId: Int?,
        metaId: Int,
        date: String
    ): Int {
        val entryId = dao.insertEntry(
            EntryEntity(
                leaderCardId = leader.cardId,
                title = title,
                locationId = locationId,
                metaId = metaId,
                date = date
            )
        ).toInt()
        dao.insertRounds(rounds.toRoundEntities(entryId))
        return entryId
    }

    override suspend fun updateEntry(
        entryId: Int,
        leader: Leader,
        rounds: List<Round>,
        title: String?,
        locationId: Int?,
        metaId: Int,
        date: String
    ) {
        dao.updateEntry(
            EntryEntity(
                id = entryId,
                leaderCardId = leader.cardId,
                title = title,
                locationId = locationId,
                metaId = metaId,
                date = date
            )
        )
        dao.deleteRoundsByEntryId(entryId)
        dao.insertRounds(rounds.toRoundEntities(entryId))
    }

    private fun List<Round>.toRoundEntities(entryId: Int) = map { r ->
        RoundEntity(
            entryId = entryId,
            roundNumber = r.roundNumber,
            leaderCardId = r.leader.cardId,
            roundResult = r.roundResult,
            turnOrder = r.turnOrder,
            dieRoll = r.dieRoll
        )
    }

    override fun getAllEntries(): Flow<List<EntryWithRounds>> = dao.getAllEntries()

    override suspend fun getEntries(): List<EntryWithRounds> = dao.getEntries()

    override suspend fun getEntryById(id: Int): EntryWithRounds? = dao.getEntryById(id)

    override suspend fun deleteEntry(entryId: Int) {
        dao.deleteRoundsByEntryId(entryId)
        dao.deleteEntry(entryId)
    }

    override suspend fun deleteAllEntries() = dao.deleteAllEntries()
}
