package commanderpepper.labophase.data

import commanderpepper.labophase.data.entity.RoundEntity
import commanderpepper.labophase.models.Leader
import commanderpepper.labophase.models.Meta
import commanderpepper.labophase.models.Round
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class EntryRepositoryImplTest {

    private val dao: EntryDao = mockk()
    private val repository = EntryRepositoryImpl(dao)

    private suspend fun savedRounds(vararg rounds: Round): List<RoundEntity> {
        val captured = slot<List<RoundEntity>>()
        coEvery { dao.insertEntryWithRounds(any(), capture(captured)) } returns 1
        repository.saveEntry(
            leader = Leader.UGLuffy,
            rounds = rounds.toList(),
            metaId = Meta.OP18.id,
            date = "2026-11-19"
        )
        return captured.captured
    }

    @Test
    fun `saveEntry keeps round notes`() = runTest {
        val rounds = savedRounds(Round(roundId = 0, roundNumber = 1, notes = "Went to time"))
        assertEquals("Went to time", rounds.single().notes)
    }

    @Test
    fun `saveEntry trims surrounding whitespace from notes`() = runTest {
        val rounds = savedRounds(Round(roundId = 0, roundNumber = 1, notes = "  Went to time \n"))
        assertEquals("Went to time", rounds.single().notes)
    }

    @Test
    fun `saveEntry stores blank notes as null`() = runTest {
        val rounds = savedRounds(
            Round(roundId = 0, roundNumber = 1, notes = "   "),
            Round(roundId = 1, roundNumber = 2, notes = null)
        )
        assertNull(rounds[0].notes)
        assertNull(rounds[1].notes)
    }
}
