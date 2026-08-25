package commanderpepper.labophase.screens.entries

import app.cash.turbine.test
import commanderpepper.labophase.data.entity.EntryEntity
import commanderpepper.labophase.data.EntryRepository
import commanderpepper.labophase.data.EntryWithRounds
import commanderpepper.labophase.data.OpponentRepository
import commanderpepper.labophase.data.entity.RoundEntity
import commanderpepper.labophase.models.Opponent
import commanderpepper.labophase.models.RoundResult
import commanderpepper.labophase.models.TurnOrder
import commanderpepper.labophase.logic.PunkRecordCreatorImpl
import commanderpepper.labophase.logic.converter.EntryToEntrySelectionUIConverter
import commanderpepper.labophase.models.Leader
import commanderpepper.labophase.models.Meta
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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
class EntrySelectionViewModelImplTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val entryRepository: EntryRepository = mockk()
    private val opponentRepository: OpponentRepository = mockk()
    private val converter = EntryToEntrySelectionUIConverter(PunkRecordCreatorImpl())

    private val entriesFlow = MutableStateFlow<List<EntryWithRounds>>(emptyList())
    private val opponentsFlow = MutableStateFlow<List<Opponent>>(emptyList())

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { entryRepository.getAllEntries() } returns entriesFlow
        every { opponentRepository.getOpponents() } returns opponentsFlow
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun makeEntry(
        id: Int = 1,
        leaderCardId: String = Leader.UGLuffy.cardId,
        rounds: List<RoundEntity> = emptyList(),
        metaId: Int? = null
    ) = EntryWithRounds(entry = EntryEntity(id = id, leaderCardId = leaderCardId, metaId = metaId), rounds = rounds)

    private fun makeRound(
        entryId: Int = 1,
        leaderCardId: String = Leader.RShanks.cardId,
        result: RoundResult = RoundResult.Win,
        order: TurnOrder = TurnOrder.First
    ) = RoundEntity(entryId = entryId, roundNumber = 1, leaderCardId = leaderCardId, roundResult = result, turnOrder = order)

    private fun createViewModel() = EntrySelectionViewModelImpl(entryRepository, converter, opponentRepository)

    @Test
    fun `initial state is loading`() = runTest {
        // SharedFlow has no initial value — combine won't emit until entries emits
        every { entryRepository.getAllEntries() } returns MutableSharedFlow()
        val vm = createViewModel()

        vm.entrySelectionUiState.test {
            val state = awaitItem()
            assertTrue(state.isLoading)
            assertTrue(state.entries.isEmpty())
            assertNull(state.errorMessage)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits entries when repository emits`() = runTest {
        val vm = createViewModel()

        vm.entrySelectionUiState.test {
            awaitItem() // initial empty entries state

            entriesFlow.value = listOf(makeEntry(id = 1))
            val state = awaitItem()

            assertFalse(state.isLoading)
            assertNull(state.errorMessage)
            assertEquals(1, state.entries.size)
            assertEquals(1, state.entries[0].entryId)
            assertEquals(Leader.UGLuffy, state.entries[0].leader)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `wins and losses are converted correctly`() = runTest {
        val vm = createViewModel()
        val rounds = listOf(
            makeRound(result = RoundResult.Win),
            makeRound(result = RoundResult.Win),
            makeRound(result = RoundResult.Loss)
        )

        vm.entrySelectionUiState.test {
            awaitItem() // initial empty entries state

            entriesFlow.value = listOf(makeEntry(rounds = rounds))
            val state = awaitItem()

            assertEquals(2, state.entries[0].wins)
            assertEquals(1, state.entries[0].losses)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updated repository emission replaces previous entries`() = runTest {
        val vm = createViewModel()

        vm.entrySelectionUiState.test {
            awaitItem() // initial empty entries state

            entriesFlow.value = listOf(makeEntry(id = 1))
            awaitItem() // first populated emission

            entriesFlow.value = listOf(makeEntry(id = 1), makeEntry(id = 2))
            val state = awaitItem()

            assertEquals(2, state.entries.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clearing entries emits empty list`() = runTest {
        val vm = createViewModel()

        vm.entrySelectionUiState.test {
            awaitItem() // initial empty entries state

            entriesFlow.value = listOf(makeEntry(id = 1))
            awaitItem() // populated

            entriesFlow.value = emptyList()
            val state = awaitItem()

            assertTrue(state.entries.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `multiple entries are all converted`() = runTest {
        val vm = createViewModel()

        vm.entrySelectionUiState.test {
            awaitItem() // initial empty entries state

            entriesFlow.value = listOf(
                makeEntry(id = 1, leaderCardId = Leader.UGLuffy.cardId),
                makeEntry(id = 2, leaderCardId = Leader.RShanks.cardId)
            )
            val state = awaitItem()

            assertEquals(2, state.entries.size)
            assertEquals(Leader.UGLuffy, state.entries[0].leader)
            assertEquals(Leader.RShanks, state.entries[1].leader)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `entries with OP17 metaId preserve metaId in UI state`() = runTest {
        val vm = createViewModel()

        vm.entrySelectionUiState.test {
            awaitItem() // initial empty state

            entriesFlow.value = listOf(makeEntry(id = 1, metaId = Meta.OP17.id))
            val state = awaitItem()

            assertEquals(Meta.OP17.id, state.entries[0].metaId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `entries with null metaId preserve null metaId in UI state`() = runTest {
        val vm = createViewModel()

        vm.entrySelectionUiState.test {
            awaitItem() // initial empty state

            entriesFlow.value = listOf(makeEntry(id = 1, metaId = null))
            val state = awaitItem()

            assertEquals(null, state.entries[0].metaId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `entries from different metas are all included in UI state`() = runTest {
        val vm = createViewModel()

        vm.entrySelectionUiState.test {
            awaitItem() // initial empty state

            entriesFlow.value = listOf(
                makeEntry(id = 1, metaId = Meta.OP17.id),
                makeEntry(id = 2, metaId = Meta.OP16.id),
                makeEntry(id = 3, metaId = null)
            )
            val state = awaitItem()

            assertEquals(3, state.entries.size)
            assertEquals(Meta.OP17.id, state.entries.first { it.entryId == 1 }.metaId)
            assertEquals(Meta.OP16.id, state.entries.first { it.entryId == 2 }.metaId)
            assertNull(state.entries.first { it.entryId == 3 }.metaId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `error state is emitted when repository throws`() = runTest {
        every { entryRepository.getAllEntries() } returns flow { throw RuntimeException("db error") }
        val vm = createViewModel()

        vm.entrySelectionUiState.test {
            val state = awaitItem()
            assertNotNull(state.errorMessage)
            assertTrue(state.entries.isEmpty())
            assertFalse(state.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
