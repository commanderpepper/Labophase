package commanderpepper.labophase.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {
    @Insert
    suspend fun insertEntry(entry: EntryEntity): Long

    @Insert
    suspend fun insertRounds(rounds: List<RoundEntity>)

    @Transaction
    @Query("SELECT * FROM entries ORDER BY id DESC")
    fun getAllEntries(): Flow<List<EntryWithRounds>>

    @Transaction
    @Query("SELECT * FROM entries ORDER BY id DESC")
    fun getEntries(): List<EntryWithRounds>

    @Transaction
    @Query("SELECT * FROM entries WHERE id = :id")
    suspend fun getEntryById(id: Int): EntryWithRounds?
}
