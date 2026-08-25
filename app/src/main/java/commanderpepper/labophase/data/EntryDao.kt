package commanderpepper.labophase.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import commanderpepper.labophase.data.entity.EntryEntity
import commanderpepper.labophase.data.entity.RoundEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class EntryDao {
    @Insert
    abstract suspend fun insertEntry(entry: EntryEntity): Long

    @Update
    abstract suspend fun updateEntry(entry: EntryEntity)

    @Insert
    abstract suspend fun insertRounds(rounds: List<RoundEntity>)

    @Query("DELETE FROM rounds WHERE entryId = :entryId")
    abstract suspend fun deleteRoundsByEntryId(entryId: Int)

    @Transaction
    @Query("SELECT * FROM entries ORDER BY id DESC")
    abstract fun getAllEntries(): Flow<List<EntryWithRounds>>

    @Transaction
    @Query("SELECT * FROM entries ORDER BY id DESC")
    abstract suspend fun getEntries(): List<EntryWithRounds>

    @Transaction
    @Query("SELECT * FROM entries WHERE id = :id")
    abstract suspend fun getEntryById(id: Int): EntryWithRounds?

    @Query("DELETE FROM entries WHERE id = :entryId")
    abstract suspend fun deleteEntry(entryId: Int)

    @Query("DELETE FROM entries")
    abstract suspend fun deleteAllEntries()

    @Transaction
    open suspend fun insertEntryWithRounds(entry: EntryEntity, rounds: List<RoundEntity>): Int {
        val entryId = insertEntry(entry).toInt()
        insertRounds(rounds.map { it.copy(entryId = entryId) })
        return entryId
    }

    @Transaction
    open suspend fun replaceRoundsForEntry(entryId: Int, entry: EntryEntity, rounds: List<RoundEntity>) {
        updateEntry(entry)
        deleteRoundsByEntryId(entryId)
        insertRounds(rounds)
    }
}
