package commanderpepper.labophase.screens.roundentry

import commanderpepper.labophase.data.EntryEntity
import commanderpepper.labophase.data.EntryRepository
import commanderpepper.labophase.data.EntryWithRounds
import commanderpepper.labophase.data.RoundEntity
import commanderpepper.labophase.logic.LeaderOrderDecider
import commanderpepper.labophase.models.Leader
import io.mockk.coEvery
import io.mockk.coVerify
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
class RoundEntryViewModelImplTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val leaderOrderDecider: LeaderOrderDecider = mockk()
    private val entryRepository: EntryRepository = mockk(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { leaderOrderDecider.getPlayerLeaderSelect() } returns emptyList()
        coEvery { leaderOrderDecider.getRoundLeaderSelect() } returns emptyList()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(entryId: Int = -1) =
        RoundEntryViewModelImpl(leaderOrderDecider, entryRepository, entryId)

    @Test
    fun `initial leader selected is UGLuffy`() = runTest {
        val vm = createViewModel()
        assertEquals(Leader.UGLuffy, vm.leaderSelected.value)
    }

    @Test
    fun `initial rounds list is empty`() = runTest {
        val vm = createViewModel()
        assertTrue(vm.rounds.value.isEmpty())
    }

    @Test
    fun `initial punk record is empty`() = runTest {
        val vm = createViewModel()
        assertTrue(vm.punkRecordEntry.value.isEmpty())
    }

    @Test
    fun `addNewRound adds one round`() = runTest {
        val vm = createViewModel()

        vm.addNewRound()

        assertEquals(1, vm.rounds.value.size)
    }

    @Test
    fun `addNewRound twice produces two rounds with distinct ids`() = runTest {
        val vm = createViewModel()

        vm.addNewRound()
        vm.addNewRound()

        val rounds = vm.rounds.value
        assertEquals(2, rounds.size)
        assertEquals(2, rounds.map { it.roundId }.toSet().size)
    }

    @Test
    fun `new round has default Win result and First turn order`() = runTest {
        val vm = createViewModel()

        vm.addNewRound()

        val round = vm.rounds.value.first()
        assertEquals("Win", round.roundResult)
        assertEquals("First", round.turnOrder)
    }

    @Test
    fun `chooseLeader updates leaderSelected`() = runTest {
        val vm = createViewModel()

        vm.chooseLeader(Leader.RShanks)

        assertEquals(Leader.RShanks, vm.leaderSelected.value)
    }

    @Test
    fun `roundLeaderSelect updates the correct round leader`() = runTest {
        val vm = createViewModel()
        vm.addNewRound()
        val roundId = vm.rounds.value.first().roundId

        vm.roundLeaderSelect(roundId, Leader.RShanks)

        assertEquals(Leader.RShanks, vm.rounds.value.first().leader)
    }

    @Test
    fun `roundResultSelect Win updates round result to Win`() = runTest {
        val vm = createViewModel()
        vm.addNewRound()
        val roundId = vm.rounds.value.first().roundId

        vm.roundResultSelect(roundId, "Win")

        assertEquals("Win", vm.rounds.value.first().roundResult)
    }

    @Test
    fun `roundResultSelect Loss updates round result to Loss`() = runTest {
        val vm = createViewModel()
        vm.addNewRound()
        val roundId = vm.rounds.value.first().roundId

        vm.roundResultSelect(roundId, "Loss")

        assertEquals("Loss", vm.rounds.value.first().roundResult)
        assertTrue(vm.rounds.value.first().summary.contains("L"))
    }

    @Test
    fun `roundTurnOrderSelect Second updates turn order to Second`() = runTest {
        val vm = createViewModel()
        vm.addNewRound()
        val roundId = vm.rounds.value.first().roundId

        vm.roundTurnOrderSelect(roundId, "Second")

        assertEquals("Second", vm.rounds.value.first().turnOrder)
        assertTrue(vm.rounds.value.first().summary.contains("2"))
    }

    @Test
    fun `roundTurnOrderSelect First updates turn order to First`() = runTest {
        val vm = createViewModel()
        vm.addNewRound()
        val roundId = vm.rounds.value.first().roundId
        vm.roundTurnOrderSelect(roundId, "Second") // set to Second first

        vm.roundTurnOrderSelect(roundId, "First")

        assertEquals("First", vm.rounds.value.first().turnOrder)
        assertTrue(vm.rounds.value.first().summary.contains("1"))
    }

    @Test
    fun `removeRound removes the correct round`() = runTest {
        val vm = createViewModel()
        vm.addNewRound()
        vm.addNewRound()
        val roundIdToRemove = vm.rounds.value.first().roundId

        vm.removeRound(roundIdToRemove)

        assertEquals(1, vm.rounds.value.size)
        assertTrue(vm.rounds.value.none { it.roundId == roundIdToRemove })
    }

    @Test
    fun `removeRound on non-existent id leaves list unchanged`() = runTest {
        val vm = createViewModel()
        vm.addNewRound()

        vm.removeRound(999)

        assertEquals(1, vm.rounds.value.size)
    }

    @Test
    fun `transformEntry generates punk record starting with PR add`() = runTest {
        val vm = createViewModel()
        vm.addNewRound()

        vm.transformEntry()

        assertTrue(vm.punkRecordEntry.value.startsWith("!PR add"))
    }

    @Test
    fun `transformEntry punk record second line is leader name`() = runTest {
        val vm = createViewModel()
        vm.chooseLeader(Leader.RShanks)
        vm.addNewRound()

        vm.transformEntry()

        val lines = vm.punkRecordEntry.value.lines()
        assertEquals(Leader.RShanks.name, lines[1])
    }

    @Test
    fun `transformEntry saves new entry when entryId is -1`() = runTest {
        val vm = createViewModel(entryId = -1)
        vm.addNewRound()

        vm.transformEntry()

        coVerify { entryRepository.saveEntry(any(), any()) }
    }

    @Test
    fun `transformEntry updates existing entry when entryId is set`() = runTest {
        val existingEntry = EntryWithRounds(
            entry = EntryEntity(id = 5, leaderCardId = Leader.UGLuffy.cardId),
            rounds = emptyList()
        )
        coEvery { entryRepository.getEntryById(5) } returns existingEntry

        val vm = createViewModel(entryId = 5)
        // Give init block time to load on IO thread
        Thread.sleep(200)

        vm.addNewRound()
        vm.transformEntry()

        coVerify { entryRepository.updateEntry(5, any(), any()) }
    }

    @Test
    fun `round summary reflects leader name and result`() = runTest {
        val vm = createViewModel()
        vm.addNewRound()
        val roundId = vm.rounds.value.first().roundId
        vm.roundLeaderSelect(roundId, Leader.RShanks)
        vm.roundResultSelect(roundId, "Win")
        vm.roundTurnOrderSelect(roundId, "First")

        val summary = vm.rounds.value.first().summary
        assertTrue(summary.contains(Leader.RShanks.name))
        assertTrue(summary.contains("W"))
        assertTrue(summary.contains("1"))
    }

    @Test
    fun `existing entry loads correct leader on init`() = runTest {
        val existingEntry = EntryWithRounds(
            entry = EntryEntity(id = 1, leaderCardId = Leader.RShanks.cardId),
            rounds = emptyList()
        )
        coEvery { entryRepository.getEntryById(1) } returns existingEntry

        val vm = createViewModel(entryId = 1)
        Thread.sleep(200) // wait for IO dispatcher to complete

        assertEquals(Leader.RShanks, vm.leaderSelected.value)
    }

    @Test
    fun `existing entry loads rounds on init`() = runTest {
        val existingEntry = EntryWithRounds(
            entry = EntryEntity(id = 1, leaderCardId = Leader.UGLuffy.cardId),
            rounds = listOf(
                RoundEntity(
                    entryId = 1, roundNumber = 1,
                    leaderCardId = Leader.RShanks.cardId,
                    roundResult = "Win", turnOrder = "First"
                )
            )
        )
        coEvery { entryRepository.getEntryById(1) } returns existingEntry

        val vm = createViewModel(entryId = 1)
        Thread.sleep(200) // wait for IO dispatcher to complete

        assertEquals(1, vm.rounds.value.size)
        assertEquals(Leader.RShanks, vm.rounds.value.first().leader)
        assertEquals("Win", vm.rounds.value.first().roundResult)
    }
}
