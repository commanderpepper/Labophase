package commanderpepper.labophase.data

import commanderpepper.labophase.data.entity.OpponentEntity
import commanderpepper.labophase.models.Opponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface OpponentRepository {
    fun getOpponents(): Flow<List<Opponent>>
    suspend fun getOpponentsList(): List<Opponent>
    suspend fun addOpponent(name: String, nickname: String)
    suspend fun updateOpponent(id: Int, name: String, nickname: String)
    suspend fun deleteOpponent(id: Int)
}

class OpponentRepositoryImpl(private val opponentDao: OpponentDao) : OpponentRepository {
    override fun getOpponents(): Flow<List<Opponent>> =
        opponentDao.getOpponents().map { list -> list.map { it.toOpponent() } }

    override suspend fun getOpponentsList(): List<Opponent> =
        opponentDao.getOpponentsList().map { it.toOpponent() }

    override suspend fun addOpponent(name: String, nickname: String) {
        opponentDao.insertOpponent(OpponentEntity(name = name, nickname = nickname))
    }

    override suspend fun updateOpponent(id: Int, name: String, nickname: String) {
        opponentDao.updateOpponent(id, name, nickname)
    }

    override suspend fun deleteOpponent(id: Int) {
        opponentDao.deleteOpponent(id)
    }

    private fun OpponentEntity.toOpponent() = Opponent(id = id, name = name, nickname = nickname)
}
