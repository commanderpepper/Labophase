package commanderpepper.labophase.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {
    @Insert
    suspend fun insertEntry(entry: EntryEntity): Long

    @Update
    suspend fun updateEntry(entry: EntryEntity)

    @Insert
    suspend fun insertRounds(rounds: List<RoundEntity>)

    @Query("DELETE FROM rounds WHERE entryId = :entryId")
    suspend fun deleteRoundsByEntryId(entryId: Int)

    @Transaction
    @Query("SELECT * FROM entries ORDER BY id DESC")
    fun getAllEntries(): Flow<List<EntryWithRounds>>

    @Transaction
    @Query("SELECT * FROM entries ORDER BY id DESC")
    fun getEntries(): List<EntryWithRounds>

    @Transaction
    @Query("SELECT * FROM entries WHERE id = :id")
    suspend fun getEntryById(id: Int): EntryWithRounds?

    @Query("DELETE FROM entries WHERE id = :entryId")
    suspend fun deleteEntry(entryId: Int)

    @Query("DELETE FROM entries")
    suspend fun deleteAllEntries()
}
