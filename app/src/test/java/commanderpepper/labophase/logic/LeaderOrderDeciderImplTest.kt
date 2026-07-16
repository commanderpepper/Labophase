package commanderpepper.labophase.logic

import commanderpepper.labophase.data.EntryEntity
import commanderpepper.labophase.data.EntryRepository
import commanderpepper.labophase.data.EntryWithRounds
import commanderpepper.labophase.data.RoundEntity
import commanderpepper.labophase.data.SettingsRepository
import commanderpepper.labophase.models.LEADERS_LIST
import commanderpepper.labophase.models.Leader
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LeaderOrderDeciderImplTest {

    private val entryRepository: EntryRepository = mockk()
    private val settingsRepository: SettingsRepository = mockk()
    private val decider = LeaderOrderDeciderImpl(entryRepository, settingsRepository)

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun makeEntry(leaderCardId: String, rounds: List<RoundEntity> = emptyList()) =
        EntryWithRounds(entry = EntryEntity(leaderCardId = leaderCardId), rounds = rounds)

    private fun makeRound(leaderCardId: String) =
        RoundEntity(entryId = 0, roundNumber = 1, leaderCardId = leaderCardId, roundResult = "Win", turnOrder = "First")

    @Test
    fun `getPlayerLeaderSelect returns all leaders when showAll is true`() = runTest {
        coEvery { settingsRepository.isShowingAllLeaders() } returns true
        coEvery { entryRepository.getEntries() } returns emptyList()

        val result = decider.getPlayerLeaderSelect()

        assertEquals(LEADERS_LIST.size, result.size)
        assertTrue(result.containsAll(LEADERS_LIST))
    }

    @Test
    fun `getPlayerLeaderSelect excludes block 1 leaders when showAll is false`() = runTest {
        coEvery { settingsRepository.isShowingAllLeaders() } returns false
        coEvery { entryRepository.getEntries() } returns emptyList()

        val result = decider.getPlayerLeaderSelect()

        assertTrue(result.none { it.blockNumber == 1 })
        assertEquals(LEADERS_LIST.filter { it.blockNumber != 1 }.size, result.size)
    }

    @Test
    fun `getRoundLeaderSelect excludes block 1 leaders when showAll is false`() = runTest {
        coEvery { settingsRepository.isShowingAllLeaders() } returns false
        coEvery { entryRepository.getEntries() } returns emptyList()

        val result = decider.getRoundLeaderSelect()

        assertTrue(result.none { it.blockNumber == 1 })
    }

    @Test
    fun `most used player leader appears first`() = runTest {
        coEvery { settingsRepository.isShowingAllLeaders() } returns true
        val preferred = Leader.UGLuffy
        coEvery { entryRepository.getEntries() } returns listOf(
            makeEntry(leaderCardId = preferred.cardId),
            makeEntry(leaderCardId = preferred.cardId),
            makeEntry(leaderCardId = Leader.PBLuffy.cardId)
        )

        val result = decider.getPlayerLeaderSelect()

        assertEquals(preferred, result.first())
    }

    @Test
    fun `most used round opponent appears first in getRoundLeaderSelect`() = runTest {
        coEvery { settingsRepository.isShowingAllLeaders() } returns true
        val frequentOpponent = Leader.RShanks
        coEvery { entryRepository.getEntries() } returns listOf(
            makeEntry(
                leaderCardId = Leader.UGLuffy.cardId,
                rounds = listOf(
                    makeRound(frequentOpponent.cardId),
                    makeRound(frequentOpponent.cardId),
                    makeRound(Leader.PBLuffy.cardId)
                )
            )
        )

        val result = decider.getRoundLeaderSelect()

        assertEquals(frequentOpponent, result.first())
    }

    @Test
    fun `getRoundLeaderSelect uses round opponent counts not player counts`() = runTest {
        coEvery { settingsRepository.isShowingAllLeaders() } returns true
        // Player is UGLuffy many times, but round opponent is RShanks once
        val frequentOpponent = Leader.RShanks
        coEvery { entryRepository.getEntries() } returns listOf(
            makeEntry(leaderCardId = Leader.UGLuffy.cardId, rounds = listOf(makeRound(frequentOpponent.cardId))),
            makeEntry(leaderCardId = Leader.UGLuffy.cardId, rounds = listOf(makeRound(frequentOpponent.cardId)))
        )

        val playerResult = decider.getPlayerLeaderSelect()
        val roundResult = decider.getRoundLeaderSelect()

        assertEquals(Leader.UGLuffy, playerResult.first())
        assertEquals(frequentOpponent, roundResult.first())
    }

    @Test
    fun `leaders with no entries are sorted by set number descending`() = runTest {
        coEvery { settingsRepository.isShowingAllLeaders() } returns true
        coEvery { entryRepository.getEntries() } returns emptyList()

        val result = decider.getPlayerLeaderSelect()

        val setNumbers = result.map { it.set.number }
        assertEquals(setNumbers.sortedDescending(), setNumbers)
    }

    @Test
    fun `player leader count does not affect round leader order`() = runTest {
        coEvery { settingsRepository.isShowingAllLeaders() } returns true
        // UGLuffy used as player 3 times, but never as opponent
        // RShanks used as opponent once
        coEvery { entryRepository.getEntries() } returns listOf(
            makeEntry(leaderCardId = Leader.UGLuffy.cardId, rounds = listOf(makeRound(Leader.RShanks.cardId))),
            makeEntry(leaderCardId = Leader.UGLuffy.cardId),
            makeEntry(leaderCardId = Leader.UGLuffy.cardId)
        )

        val result = decider.getRoundLeaderSelect()

        assertEquals(Leader.RShanks, result.first())
    }
}
