package commanderpepper.labophase.logic.converter

import commanderpepper.labophase.data.EntryEntity
import commanderpepper.labophase.data.EntryWithRounds
import commanderpepper.labophase.data.RoundEntity
import commanderpepper.labophase.models.Leader
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EntryToEntrySelectionUIConverterTest {

    private val converter = EntryToEntrySelectionUIConverter()

    private fun makeRound(
        entryId: Int = 1,
        leaderCardId: String = Leader.RShanks.cardId,
        result: String = "Win",
        order: String = "First",
        num: Int = 1
    ) = RoundEntity(entryId = entryId, roundNumber = num, leaderCardId = leaderCardId, roundResult = result, turnOrder = order)

    private fun makeEntry(
        id: Int = 1,
        leaderCardId: String = Leader.UGLuffy.cardId,
        rounds: List<RoundEntity> = emptyList()
    ) = EntryWithRounds(entry = EntryEntity(id = id, leaderCardId = leaderCardId), rounds = rounds)

    @Test
    fun `empty rounds produces zero wins and losses with empty rounds list`() {
        val result = converter.entryToEntrySelectionUI(makeEntry())
        assertEquals(0, result.wins)
        assertEquals(0, result.losses)
        assertTrue(result.rounds.isEmpty())
    }

    @Test
    fun `wins and losses are counted correctly for mixed results`() {
        val rounds = listOf(
            makeRound(result = "Win"),
            makeRound(result = "Win"),
            makeRound(result = "Loss")
        )
        val result = converter.entryToEntrySelectionUI(makeEntry(rounds = rounds))
        assertEquals(2, result.wins)
        assertEquals(1, result.losses)
    }

    @Test
    fun `punk record starts with PR add header then leader name`() {
        val result = converter.entryToEntrySelectionUI(makeEntry())
        val lines = result.punkRecord.lines()
        assertEquals("!PR add", lines[0])
        assertEquals(Leader.UGLuffy.name, lines[1])
    }

    @Test
    fun `punk record win first formats as W LeaderName 1st`() {
        val rounds = listOf(makeRound(result = "Win", order = "First"))
        val result = converter.entryToEntrySelectionUI(makeEntry(rounds = rounds))
        val roundLine = result.punkRecord.lines()[2]
        assertTrue(roundLine.startsWith("W "))
        assertTrue(roundLine.endsWith("1st"))
    }

    @Test
    fun `punk record loss second formats as L LeaderName 2nd`() {
        val rounds = listOf(makeRound(result = "Loss", order = "Second"))
        val result = converter.entryToEntrySelectionUI(makeEntry(rounds = rounds))
        val roundLine = result.punkRecord.lines()[2]
        assertTrue(roundLine.startsWith("L "))
        assertTrue(roundLine.endsWith("2nd"))
    }

    @Test
    fun `round summary formats as LeaderName W 1 for win first`() {
        val round = makeRound(leaderCardId = Leader.RShanks.cardId, result = "Win", order = "First")
        val result = converter.entryToEntrySelectionUI(makeEntry(rounds = listOf(round)))
        assertEquals("${Leader.RShanks.name}, W, 1", result.rounds[0].summary)
    }

    @Test
    fun `round summary formats as LeaderName L 2 for loss second`() {
        val round = makeRound(leaderCardId = Leader.RShanks.cardId, result = "Loss", order = "Second")
        val result = converter.entryToEntrySelectionUI(makeEntry(rounds = listOf(round)))
        assertEquals("${Leader.RShanks.name}, L, 2", result.rounds[0].summary)
    }

    @Test
    fun `entryId is preserved in UI model`() {
        val result = converter.entryToEntrySelectionUI(makeEntry(id = 42))
        assertEquals(42, result.entryId)
    }

    @Test
    fun `leader is looked up correctly from card ID`() {
        val result = converter.entryToEntrySelectionUI(makeEntry(leaderCardId = Leader.UGLuffy.cardId))
        assertEquals(Leader.UGLuffy, result.leader)
    }

    @Test
    fun `getWins counts only winning rounds`() {
        val rounds = listOf(
            makeRound(result = "Win"),
            makeRound(result = "Loss"),
            makeRound(result = "Win")
        )
        assertEquals(2, getWins(rounds))
    }

    @Test
    fun `getLosses counts only losing rounds`() {
        val rounds = listOf(
            makeRound(result = "Win"),
            makeRound(result = "Loss"),
            makeRound(result = "Loss")
        )
        assertEquals(2, getLosses(rounds))
    }

    @Test
    fun `all wins produces zero losses`() {
        val rounds = listOf(makeRound(result = "Win"), makeRound(result = "Win"))
        assertEquals(0, getLosses(rounds))
    }

    @Test
    fun `all losses produces zero wins`() {
        val rounds = listOf(makeRound(result = "Loss"), makeRound(result = "Loss"))
        assertEquals(0, getWins(rounds))
    }

    @Test
    fun `multiple rounds appear in punk record in order`() {
        val rounds = listOf(
            makeRound(leaderCardId = Leader.RShanks.cardId, result = "Win", order = "First", num = 1),
            makeRound(leaderCardId = Leader.PBLuffy.cardId, result = "Loss", order = "Second", num = 2)
        )
        val result = converter.entryToEntrySelectionUI(makeEntry(rounds = rounds))
        val lines = result.punkRecord.lines()
        assertEquals(4, lines.size)
        assertTrue(lines[2].contains(Leader.RShanks.name))
        assertTrue(lines[3].contains(Leader.PBLuffy.name))
    }
}
