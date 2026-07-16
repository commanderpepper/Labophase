package commanderpepper.labophase.screens.entries

import app.cash.turbine.test
import commanderpepper.labophase.data.EntryEntity
import commanderpepper.labophase.data.EntryRepository
import commanderpepper.labophase.data.EntryWithRounds
import commanderpepper.labophase.data.RoundEntity
import commanderpepper.labophase.logic.converter.EntryToEntrySelectionUIConverter
import commanderpepper.labophase.models.Leader
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
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
class EntrySelectionViewModelImplTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val entryRepository: EntryRepository = mockk()
    private val converter = EntryToEntrySelectionUIConverter()

    private val entriesFlow = MutableStateFlow<List<EntryWithRounds>>(emptyList())

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { entryRepository.getAllEntries() } returns entriesFlow
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun makeEntry(
        id: Int = 1,
        leaderCardId: String = Leader.UGLuffy.cardId,
        rounds: List<RoundEntity> = emptyList()
    ) = EntryWithRounds(entry = EntryEntity(id = id, leaderCardId = leaderCardId), rounds = rounds)

    private fun makeRound(
        entryId: Int = 1,
        leaderCardId: String = Leader.RShanks.cardId,
        result: String = "Win",
        order: String = "First"
    ) = RoundEntity(entryId = entryId, roundNumber = 1, leaderCardId = leaderCardId, roundResult = result, turnOrder = order)

    private fun createViewModel() = EntrySelectionViewModelImpl(entryRepository, converter)

    @Test
    fun `initial emission is empty list`() = runTest {
        val vm = createViewModel()

        vm.entries.test {
            assertTrue(awaitItem().isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits converted entry when repository emits`() = runTest {
        val vm = createViewModel()

        vm.entries.test {
            awaitItem() // initial empty

            entriesFlow.value = listOf(makeEntry(id = 1))
            val entries = awaitItem()

            assertEquals(1, entries.size)
            assertEquals(1, entries[0].entryId)
            assertEquals(Leader.UGLuffy, entries[0].leader)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `wins and losses are converted correctly`() = runTest {
        val vm = createViewModel()
        val rounds = listOf(
            makeRound(result = "Win"),
            makeRound(result = "Win"),
            makeRound(result = "Loss")
        )

        vm.entries.test {
            awaitItem() // initial empty

            entriesFlow.value = listOf(makeEntry(rounds = rounds))
            val entries = awaitItem()

            assertEquals(2, entries[0].wins)
            assertEquals(1, entries[0].losses)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updated repository emission replaces previous entries`() = runTest {
        val vm = createViewModel()

        vm.entries.test {
            awaitItem() // initial empty

            entriesFlow.value = listOf(makeEntry(id = 1))
            awaitItem() // first emission

            entriesFlow.value = listOf(makeEntry(id = 1), makeEntry(id = 2))
            val updated = awaitItem()

            assertEquals(2, updated.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clearing entries emits empty list`() = runTest {
        val vm = createViewModel()

        vm.entries.test {
            awaitItem() // initial empty

            entriesFlow.value = listOf(makeEntry(id = 1))
            awaitItem() // populated

            entriesFlow.value = emptyList()
            val cleared = awaitItem()

            assertTrue(cleared.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `multiple entries are all converted`() = runTest {
        val vm = createViewModel()

        vm.entries.test {
            awaitItem() // initial empty

            entriesFlow.value = listOf(
                makeEntry(id = 1, leaderCardId = Leader.UGLuffy.cardId),
                makeEntry(id = 2, leaderCardId = Leader.RShanks.cardId)
            )
            val entries = awaitItem()

            assertEquals(2, entries.size)
            assertEquals(Leader.UGLuffy, entries[0].leader)
            assertEquals(Leader.RShanks, entries[1].leader)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
