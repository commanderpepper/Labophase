package commanderpepper.labophase.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import commanderpepper.labophase.data.entity.OpponentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OpponentDao {
    @Query("SELECT * FROM opponents ORDER BY nickname ASC")
    fun getOpponents(): Flow<List<OpponentEntity>>

    @Query("SELECT * FROM opponents ORDER BY nickname ASC")
    suspend fun getOpponentsList(): List<OpponentEntity>

    @Insert
    suspend fun insertOpponent(opponent: OpponentEntity): Long

    @Query("UPDATE opponents SET name = :name, nickname = :nickname WHERE id = :id")
    suspend fun updateOpponent(id: Int, name: String, nickname: String)

    @Query("DELETE FROM opponents WHERE id = :id")
    suspend fun deleteOpponent(id: Int)
}
