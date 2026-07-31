package commanderpepper.labophase.screens.roundentry

import commanderpepper.labophase.data.entity.EntryEntity
import commanderpepper.labophase.data.EntryRepository
import commanderpepper.labophase.data.EntryWithRounds
import commanderpepper.labophase.data.entity.RoundEntity
import commanderpepper.labophase.models.RoundResult
import commanderpepper.labophase.models.TurnOrder
import commanderpepper.labophase.logic.LeaderOrderDecider
import commanderpepper.labophase.models.Leader
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
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

    private fun createViewModel(entryId: Int? = null) =
        RoundEntryViewModelImpl(leaderOrderDecider, entryRepository, entryId, testDispatcher)

    @Test
    fun `initial leader selected is PKatakuri`() = runTest {
        val vm = createViewModel()
        assertEquals(Leader.PKatakuri, vm.uiState.value.leaderSelected)
    }

    @Test
    fun `initial rounds list is empty`() = runTest {
        val vm = createViewModel()
        assertTrue(vm.uiState.value.rounds.isEmpty())
    }

    @Test
    fun `initial punk record is empty`() = runTest {
        val vm = createViewModel()
        assertTrue(vm.uiState.value.punkRecordEntry.isEmpty())
    }

    @Test
    fun `addNewRound adds one round`() = runTest {
        val vm = createViewModel()

        vm.addNewRound()

        assertEquals(1, vm.uiState.value.rounds.size)
    }

    @Test
    fun `addNewRound twice produces two rounds with distinct ids`() = runTest {
        val vm = createViewModel()

        vm.addNewRound()
        vm.addNewRound()

        val rounds = vm.uiState.value.rounds
        assertEquals(2, rounds.size)
        assertEquals(2, rounds.map { it.roundId }.toSet().size)
    }

    @Test
    fun `new round has default Win result and First turn order`() = runTest {
        val vm = createViewModel()

        vm.addNewRound()

        val round = vm.uiState.value.rounds.first()
        assertEquals(RoundResult.Win, round.roundResult)
        assertEquals(TurnOrder.First, round.turnOrder)
    }

    @Test
    fun `chooseLeader updates leaderSelected`() = runTest {
        val vm = createViewModel()

        vm.chooseLeader(Leader.RShanks)

        assertEquals(Leader.RShanks, vm.uiState.value.leaderSelected)
    }

    @Test
    fun `roundLeaderSelect updates the correct round leader`() = runTest {
        val vm = createViewModel()
        vm.addNewRound()
        val roundId = vm.uiState.value.rounds.first().roundId

        vm.roundLeaderSelect(roundId, Leader.RShanks)

        assertEquals(Leader.RShanks, vm.uiState.value.rounds.first().leader)
    }

    @Test
    fun `roundResultSelect Win updates round result to Win`() = runTest {
        val vm = createViewModel()
        vm.addNewRound()
        val roundId = vm.uiState.value.rounds.first().roundId

        vm.roundResultSelect(roundId, RoundResult.Win)

        assertEquals(RoundResult.Win, vm.uiState.value.rounds.first().roundResult)
    }

    @Test
    fun `roundResultSelect Loss updates round result to Loss`() = runTest {
        val vm = createViewModel()
        vm.addNewRound()
        val roundId = vm.uiState.value.rounds.first().roundId

        vm.roundResultSelect(roundId, RoundResult.Loss)

        assertEquals(RoundResult.Loss, vm.uiState.value.rounds.first().roundResult)
        assertTrue(vm.uiState.value.rounds.first().summary.contains("L"))
    }

    @Test
    fun `roundTurnOrderSelect Second updates turn order to Second`() = runTest {
        val vm = createViewModel()
        vm.addNewRound()
        val roundId = vm.uiState.value.rounds.first().roundId

        vm.roundTurnOrderSelect(roundId, TurnOrder.Second)

        assertEquals(TurnOrder.Second, vm.uiState.value.rounds.first().turnOrder)
        assertTrue(vm.uiState.value.rounds.first().summary.contains("2"))
    }

    @Test
    fun `roundTurnOrderSelect First updates turn order to First`() = runTest {
        val vm = createViewModel()
        vm.addNewRound()
        val roundId = vm.uiState.value.rounds.first().roundId
        vm.roundTurnOrderSelect(roundId, TurnOrder.Second) // set to Second first

        vm.roundTurnOrderSelect(roundId, TurnOrder.First)

        assertEquals(TurnOrder.First, vm.uiState.value.rounds.first().turnOrder)
        assertTrue(vm.uiState.value.rounds.first().summary.contains("1"))
    }

    @Test
    fun `removeRound removes the correct round`() = runTest {
        val vm = createViewModel()
        vm.addNewRound()
        vm.addNewRound()
        val roundIdToRemove = vm.uiState.value.rounds.first().roundId

        vm.removeRound(roundIdToRemove)

        assertEquals(1, vm.uiState.value.rounds.size)
        assertTrue(vm.uiState.value.rounds.none { it.roundId == roundIdToRemove })
    }

    @Test
    fun `removeRound on non-existent id leaves list unchanged`() = runTest {
        val vm = createViewModel()
        vm.addNewRound()

        vm.removeRound(999)

        assertEquals(1, vm.uiState.value.rounds.size)
    }

    @Test
    fun `transformEntry generates punk record starting with PR add`() = runTest {
        val vm = createViewModel()
        vm.addNewRound()

        vm.transformEntry()

        assertTrue(vm.uiState.value.punkRecordEntry.startsWith("!PR add"))
    }

    @Test
    fun `transformEntry punk record second line is leader name`() = runTest {
        val vm = createViewModel()
        vm.chooseLeader(Leader.RShanks)
        vm.addNewRound()

        vm.transformEntry()

        val lines = vm.uiState.value.punkRecordEntry.lines()
        assertEquals(Leader.RShanks.name, lines[1])
    }

    @Test
    fun `transformEntry saves new entry when entryId is -1`() = runTest {
        val vm = createViewModel(entryId = null)
        vm.addNewRound()

        vm.transformEntry()

        coVerify { entryRepository.saveEntry(any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `transformEntry updates existing entry when entryId is set`() = runTest {
        val existingEntry = EntryWithRounds(
            entry = EntryEntity(id = 5, leaderCardId = Leader.UGLuffy.cardId),
            rounds = emptyList()
        )
        coEvery { entryRepository.getEntryById(5) } returns existingEntry

        val vm = createViewModel(entryId = 5)
        advanceUntilIdle()

        vm.addNewRound()
        vm.transformEntry()

        coVerify { entryRepository.updateEntry(5, any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `transformEntry called twice on new entry creates once then updates`() = runTest {
        val savedId = 42
        coEvery { entryRepository.saveEntry(any(), any(), any(), any(), any(), any()) } returns savedId
        val vm = createViewModel(entryId = null)
        vm.addNewRound()

        vm.transformEntry()
        advanceUntilIdle()
        vm.transformEntry()
        advanceUntilIdle()

        coVerify(exactly = 1) { entryRepository.saveEntry(any(), any(), any(), any(), any(), any()) }
        coVerify(exactly = 1) { entryRepository.updateEntry(savedId, any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `round summary reflects leader name and result`() = runTest {
        val vm = createViewModel()
        vm.addNewRound()
        val roundId = vm.uiState.value.rounds.first().roundId
        vm.roundLeaderSelect(roundId, Leader.RShanks)
        vm.roundResultSelect(roundId, RoundResult.Win)
        vm.roundTurnOrderSelect(roundId, TurnOrder.First)

        val summary = vm.uiState.value.rounds.first().summary
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
        advanceUntilIdle()

        assertEquals(Leader.RShanks, vm.uiState.value.leaderSelected)
    }

    @Test
    fun `existing entry loads rounds on init`() = runTest {
        val existingEntry = EntryWithRounds(
            entry = EntryEntity(id = 1, leaderCardId = Leader.UGLuffy.cardId),
            rounds = listOf(
                RoundEntity(
                    entryId = 1, roundNumber = 1,
                    leaderCardId = Leader.RShanks.cardId,
                    roundResult = RoundResult.Win, turnOrder = TurnOrder.First
                )
            )
        )
        coEvery { entryRepository.getEntryById(1) } returns existingEntry

        val vm = createViewModel(entryId = 1)
        advanceUntilIdle()

        assertEquals(1, vm.uiState.value.rounds.size)
        assertEquals(Leader.RShanks, vm.uiState.value.rounds.first().leader)
        assertEquals(RoundResult.Win, vm.uiState.value.rounds.first().roundResult)
    }

    @Test
    fun `isLoading is false and errorMessage is null after init completes`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()
        assertFalse(vm.uiState.value.isLoading)
        assertNull(vm.uiState.value.errorMessage)
    }

    @Test
    fun `error message is set and isLoading is false when repository throws`() = runTest {
        coEvery { leaderOrderDecider.getPlayerLeaderSelect() } throws RuntimeException("db error")
        val vm = createViewModel()
        advanceUntilIdle()
        assertFalse(vm.uiState.value.isLoading)
        assertNotNull(vm.uiState.value.errorMessage)
    }
}
