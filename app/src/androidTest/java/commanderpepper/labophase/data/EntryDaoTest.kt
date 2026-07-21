package commanderpepper.labophase.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import commanderpepper.labophase.models.RoundResult
import commanderpepper.labophase.models.TurnOrder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EntryDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: EntryDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = db.entryDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    private suspend fun insertEntry(cardId: String = "OP16-022"): Int =
        dao.insertEntry(EntryEntity(leaderCardId = cardId)).toInt()

    private suspend fun insertRound(
        entryId: Int,
        result: RoundResult = RoundResult.Win,
        order: TurnOrder = TurnOrder.First
    ) = dao.insertRounds(
        listOf(
            RoundEntity(
                entryId = entryId,
                roundNumber = 1,
                leaderCardId = "OP09-001",
                roundResult = result,
                turnOrder = order
            )
        )
    )

    @Test
    fun insertEntry_getEntryById_roundTripsLeaderCardId() = runBlocking {
        val id = insertEntry("OP09-001")
        val result = dao.getEntryById(id)
        assertNotNull(result)
        assertEquals("OP09-001", result!!.entry.leaderCardId)
    }

    @Test
    fun getEntryById_returnsNullForUnknownId() = runBlocking {
        assertNull(dao.getEntryById(999))
    }

    @Test
    fun insertEntryAndRounds_getEntries_returnsCorrectRoundCount() = runBlocking {
        val id = insertEntry()
        insertRound(id)
        insertRound(id)
        val entries = dao.getEntries()
        assertEquals(1, entries.size)
        assertEquals(2, entries[0].rounds.size)
    }

    @Test
    fun typeConverter_roundResultWinAndTurnOrderFirst_surviveRoundTrip() = runBlocking {
        val id = insertEntry()
        insertRound(id, result = RoundResult.Win, order = TurnOrder.First)
        val round = dao.getEntries()[0].rounds[0]
        assertEquals(RoundResult.Win, round.roundResult)
        assertEquals(TurnOrder.First, round.turnOrder)
    }

    @Test
    fun typeConverter_roundResultLossAndTurnOrderSecond_surviveRoundTrip() = runBlocking {
        val id = insertEntry()
        insertRound(id, result = RoundResult.Loss, order = TurnOrder.Second)
        val round = dao.getEntries()[0].rounds[0]
        assertEquals(RoundResult.Loss, round.roundResult)
        assertEquals(TurnOrder.Second, round.turnOrder)
    }

    @Test
    fun getAllEntries_emitsInsertedEntry() = runBlocking {
        insertEntry("OP16-022")
        val result = dao.getAllEntries().first()
        assertEquals(1, result.size)
        assertEquals("OP16-022", result[0].entry.leaderCardId)
    }

    @Test
    fun updateEntry_changesLeaderCardId() = runBlocking {
        val id = insertEntry("OP16-022")
        val updated = dao.getEntryById(id)!!.entry.copy(leaderCardId = "OP09-001")
        dao.updateEntry(updated)
        assertEquals("OP09-001", dao.getEntryById(id)!!.entry.leaderCardId)
    }

    @Test
    fun deleteEntry_removesEntryAndCascadesToRounds() = runBlocking {
        val id = insertEntry()
        insertRound(id)
        dao.deleteEntry(id)
        assertNull(dao.getEntryById(id))
        assertEquals(0, dao.getEntries().size)
    }

    @Test
    fun deleteRoundsByEntryId_removesOnlyRoundsForThatEntry() = runBlocking {
        val id1 = insertEntry("OP16-022")
        val id2 = insertEntry("OP09-001")
        insertRound(id1)
        insertRound(id2)
        dao.deleteRoundsByEntryId(id1)
        val entries = dao.getEntries()
        val entry1 = entries.first { it.entry.id == id1 }
        val entry2 = entries.first { it.entry.id == id2 }
        assertEquals(0, entry1.rounds.size)
        assertEquals(1, entry2.rounds.size)
    }

    @Test
    fun deleteAllEntries_leavesEmptyDatabase() = runBlocking {
        insertEntry()
        insertEntry("OP09-001")
        dao.deleteAllEntries()
        assertEquals(0, dao.getEntries().size)
    }
}
