package commanderpepper.labophase.screens.stats

import app.cash.turbine.test
import commanderpepper.labophase.data.EntryRepository
import commanderpepper.labophase.data.EntryWithRounds
import commanderpepper.labophase.data.entity.EntryEntity
import commanderpepper.labophase.data.entity.RoundEntity
import commanderpepper.labophase.models.Leader
import commanderpepper.labophase.models.Location
import commanderpepper.labophase.models.Meta
import commanderpepper.labophase.models.RoundResult
import commanderpepper.labophase.models.TurnOrder
import commanderpepper.labophase.screens.stats.models.LeaderSelectedOption
import commanderpepper.labophase.screens.stats.models.LocationOption
import commanderpepper.labophase.screens.stats.models.MetaOption
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
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
class StatsViewModelImplTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val entryRepository: EntryRepository = mockk()
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
        metaId: Int? = Meta.OP16.id,
        locationId: Int? = null,
        rounds: List<RoundEntity> = emptyList()
    ) = EntryWithRounds(
        entry = EntryEntity(id = id, leaderCardId = leaderCardId, metaId = metaId, locationId = locationId),
        rounds = rounds
    )

    private fun makeRound(
        opponentCardId: String = Leader.RShanks.cardId,
        result: RoundResult = RoundResult.Win,
        turnOrder: TurnOrder = TurnOrder.First
    ) = RoundEntity(
        entryId = 1,
        roundNumber = 1,
        leaderCardId = opponentCardId,
        roundResult = result,
        turnOrder = turnOrder
    )

    private fun createViewModel() = StatsViewModelImpl(entryRepository)

    @Test
    fun `initial state is loading`() = runTest {
        every { entryRepository.getAllEntries() } returns MutableSharedFlow()
        val vm = createViewModel()

        vm.statsUIState.test {
            val state = awaitItem()
            assertTrue(state.isLoading)
            assertTrue(state.leadersPlayed.isEmpty())
            assertTrue(state.leadersPlayerAgainst.isEmpty())
            assertNull(state.errorMessage)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `empty entries result in empty leaders lists with no loading`() = runTest {
        val vm = createViewModel()

        vm.statsUIState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertNull(state.errorMessage)
            assertTrue(state.leadersPlayed.isEmpty())
            assertTrue(state.leadersPlayerAgainst.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `leaders played contains distinct leaders across all entries`() = runTest {
        val vm = createViewModel()

        vm.statsUIState.test {
            awaitItem()

            entriesFlow.value = listOf(
                makeEntry(id = 1, leaderCardId = Leader.UGLuffy.cardId),
                makeEntry(id = 2, leaderCardId = Leader.RShanks.cardId),
                makeEntry(id = 3, leaderCardId = Leader.UGLuffy.cardId)
            )
            val state = awaitItem()

            assertEquals(2, state.leadersPlayed.size)
            assertTrue(state.leadersPlayed.contains(Leader.UGLuffy))
            assertTrue(state.leadersPlayed.contains(Leader.RShanks))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `wins and losses against an opponent are counted correctly`() = runTest {
        val vm = createViewModel()

        vm.statsUIState.test {
            awaitItem()

            entriesFlow.value = listOf(
                makeEntry(rounds = listOf(
                    makeRound(result = RoundResult.Win),
                    makeRound(result = RoundResult.Win),
                    makeRound(result = RoundResult.Loss)
                ))
            )
            val state = awaitItem()

            val opponent = state.leadersPlayerAgainst.single()
            assertEquals(Leader.RShanks, opponent.leader)
            assertEquals(2, opponent.wins)
            assertEquals(1, opponent.losses)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `win percentage uses integer division`() = runTest {
        val vm = createViewModel()

        vm.statsUIState.test {
            awaitItem()

            entriesFlow.value = listOf(
                makeEntry(rounds = listOf(
                    makeRound(result = RoundResult.Win),
                    makeRound(result = RoundResult.Win),
                    makeRound(result = RoundResult.Loss)
                ))
            )
            val state = awaitItem()

            assertEquals("66%", state.leadersPlayerAgainst.single().percentage)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `going first wins and losses are tracked independently of second`() = runTest {
        val vm = createViewModel()

        vm.statsUIState.test {
            awaitItem()

            entriesFlow.value = listOf(
                makeEntry(rounds = listOf(
                    makeRound(result = RoundResult.Win,  turnOrder = TurnOrder.First),
                    makeRound(result = RoundResult.Loss, turnOrder = TurnOrder.First),
                    makeRound(result = RoundResult.Win,  turnOrder = TurnOrder.Second)
                ))
            )
            val state = awaitItem()

            val opponent = state.leadersPlayerAgainst.single()
            assertEquals(1, opponent.firstWins)
            assertEquals(1, opponent.firstLosses)
            assertEquals("50%", opponent.firstPercentage)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `going second wins and losses are tracked independently of first`() = runTest {
        val vm = createViewModel()

        vm.statsUIState.test {
            awaitItem()

            entriesFlow.value = listOf(
                makeEntry(rounds = listOf(
                    makeRound(result = RoundResult.Win,  turnOrder = TurnOrder.First),
                    makeRound(result = RoundResult.Win,  turnOrder = TurnOrder.Second),
                    makeRound(result = RoundResult.Loss, turnOrder = TurnOrder.Second)
                ))
            )
            val state = awaitItem()

            val opponent = state.leadersPlayerAgainst.single()
            assertEquals(1, opponent.secondWins)
            assertEquals(1, opponent.secondLosses)
            assertEquals("50%", opponent.secondPercentage)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `turn order percentage is N_A when no rounds played in that turn order`() = runTest {
        val vm = createViewModel()

        vm.statsUIState.test {
            awaitItem()

            entriesFlow.value = listOf(
                makeEntry(rounds = listOf(
                    makeRound(result = RoundResult.Win, turnOrder = TurnOrder.First)
                ))
            )
            val state = awaitItem()

            val opponent = state.leadersPlayerAgainst.single()
            assertEquals("100%", opponent.firstPercentage)
            assertEquals("N/A", opponent.secondPercentage)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `leaders against are sorted by total games descending`() = runTest {
        val vm = createViewModel()

        vm.statsUIState.test {
            awaitItem()

            entriesFlow.value = listOf(
                makeEntry(rounds = listOf(
                    makeRound(opponentCardId = Leader.RShanks.cardId, result = RoundResult.Win),
                    makeRound(opponentCardId = Leader.PBLuffy.cardId, result = RoundResult.Win),
                    makeRound(opponentCardId = Leader.PBLuffy.cardId, result = RoundResult.Loss),
                    makeRound(opponentCardId = Leader.PBLuffy.cardId, result = RoundResult.Win)
                ))
            )
            val state = awaitItem()

            assertEquals(2, state.leadersPlayerAgainst.size)
            assertEquals(Leader.PBLuffy, state.leadersPlayerAgainst[0].leader) // 3 games
            assertEquals(Leader.RShanks, state.leadersPlayerAgainst[1].leader) // 1 game
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `leader filter includes only rounds from entries with matching leader`() = runTest {
        val vm = createViewModel()

        vm.statsUIState.test {
            awaitItem()

            entriesFlow.value = listOf(
                makeEntry(id = 1, leaderCardId = Leader.UGLuffy.cardId, rounds = listOf(
                    makeRound(result = RoundResult.Win)
                )),
                makeEntry(id = 2, leaderCardId = Leader.PBLuffy.cardId, rounds = listOf(
                    makeRound(result = RoundResult.Loss)
                ))
            )
            awaitItem()

            vm.leaderSelected(Leader.UGLuffy)
            val state = awaitItem()

            val opponent = state.leadersPlayerAgainst.single()
            assertEquals(1, opponent.wins)
            assertEquals(0, opponent.losses)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `leader selected state holds correct aggregate stats for that leader`() = runTest {
        val vm = createViewModel()

        vm.statsUIState.test {
            awaitItem()

            entriesFlow.value = listOf(
                makeEntry(leaderCardId = Leader.UGLuffy.cardId, rounds = listOf(
                    makeRound(result = RoundResult.Win),
                    makeRound(result = RoundResult.Win),
                    makeRound(result = RoundResult.Loss)
                ))
            )
            awaitItem()

            vm.leaderSelected(Leader.UGLuffy)
            val state = awaitItem()

            val leaderState = state.leaderSelected as LeaderSelectedOption.LeaderSelected
            assertEquals(Leader.UGLuffy, leaderState.leader.leader)
            assertEquals(2, leaderState.leader.wins)
            assertEquals(1, leaderState.leader.losses)
            assertEquals("66%", leaderState.leader.percentage)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `all leaders selected clears filter and shows all rounds`() = runTest {
        val vm = createViewModel()

        vm.statsUIState.test {
            awaitItem()

            entriesFlow.value = listOf(
                makeEntry(id = 1, leaderCardId = Leader.UGLuffy.cardId, rounds = listOf(makeRound(result = RoundResult.Win))),
                makeEntry(id = 2, leaderCardId = Leader.PBLuffy.cardId, rounds = listOf(makeRound(result = RoundResult.Loss)))
            )
            awaitItem()

            vm.leaderSelected(Leader.UGLuffy)
            awaitItem()

            vm.allLeadersSelected()
            val state = awaitItem()

            assertTrue(state.leaderSelected is LeaderSelectedOption.All)
            val opponent = state.leadersPlayerAgainst.single()
            assertEquals(1, opponent.wins)
            assertEquals(1, opponent.losses)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `meta filter includes only entries with matching meta`() = runTest {
        val vm = createViewModel()

        vm.statsUIState.test {
            awaitItem()

            entriesFlow.value = listOf(
                makeEntry(id = 1, metaId = Meta.OP16.id, rounds = listOf(makeRound(result = RoundResult.Win))),
                makeEntry(id = 2, metaId = Meta.OP16WithST.id, rounds = listOf(makeRound(result = RoundResult.Loss)))
            )
            awaitItem()

            vm.metaSelected(Meta.OP16)
            val state = awaitItem()

            assertTrue(state.metaSelected is MetaOption.SpecificMeta)
            val opponent = state.leadersPlayerAgainst.single()
            assertEquals(1, opponent.wins)
            assertEquals(0, opponent.losses)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `all meta selected clears meta filter`() = runTest {
        val vm = createViewModel()

        vm.statsUIState.test {
            awaitItem()

            entriesFlow.value = listOf(
                makeEntry(id = 1, metaId = Meta.OP16.id, rounds = listOf(makeRound(result = RoundResult.Win))),
                makeEntry(id = 2, metaId = Meta.OP16WithST.id, rounds = listOf(makeRound(result = RoundResult.Loss)))
            )
            awaitItem()

            vm.metaSelected(Meta.OP16)
            awaitItem()

            vm.allMetaSelected()
            val state = awaitItem()

            assertTrue(state.metaSelected is MetaOption.All)
            val opponent = state.leadersPlayerAgainst.single()
            assertEquals(1, opponent.wins)
            assertEquals(1, opponent.losses)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `location filter includes only entries with matching location`() = runTest {
        val vm = createViewModel()

        vm.statsUIState.test {
            awaitItem()

            entriesFlow.value = listOf(
                makeEntry(id = 1, locationId = Location.ChronosGamesAndGifts.id, rounds = listOf(makeRound(result = RoundResult.Win))),
                makeEntry(id = 2, locationId = Location.TheClubhouse.id, rounds = listOf(makeRound(result = RoundResult.Loss)))
            )
            awaitItem()

            vm.locationSelected(Location.ChronosGamesAndGifts)
            val state = awaitItem()

            assertTrue(state.locationSelected is LocationOption.SpecificLocation)
            val opponent = state.leadersPlayerAgainst.single()
            assertEquals(1, opponent.wins)
            assertEquals(0, opponent.losses)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `all location selected clears location filter`() = runTest {
        val vm = createViewModel()

        vm.statsUIState.test {
            awaitItem()

            entriesFlow.value = listOf(
                makeEntry(id = 1, locationId = Location.ChronosGamesAndGifts.id, rounds = listOf(makeRound(result = RoundResult.Win))),
                makeEntry(id = 2, locationId = Location.TheClubhouse.id, rounds = listOf(makeRound(result = RoundResult.Loss)))
            )
            awaitItem()

            vm.locationSelected(Location.ChronosGamesAndGifts)
            awaitItem()

            vm.allLocationSelected()
            val state = awaitItem()

            assertTrue(state.locationSelected is LocationOption.All)
            val opponent = state.leadersPlayerAgainst.single()
            assertEquals(1, opponent.wins)
            assertEquals(1, opponent.losses)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `combined leader and meta filter applies both constraints`() = runTest {
        val vm = createViewModel()

        vm.statsUIState.test {
            awaitItem()

            entriesFlow.value = listOf(
                makeEntry(id = 1, leaderCardId = Leader.UGLuffy.cardId, metaId = Meta.OP16.id,
                    rounds = listOf(makeRound(result = RoundResult.Win))),
                makeEntry(id = 2, leaderCardId = Leader.UGLuffy.cardId, metaId = Meta.OP16WithST.id,
                    rounds = listOf(makeRound(result = RoundResult.Loss))),
                makeEntry(id = 3, leaderCardId = Leader.PBLuffy.cardId, metaId = Meta.OP16.id,
                    rounds = listOf(makeRound(result = RoundResult.Loss)))
            )
            awaitItem()

            vm.leaderSelected(Leader.UGLuffy)
            awaitItem()
            vm.metaSelected(Meta.OP16)
            val state = awaitItem()

            val opponent = state.leadersPlayerAgainst.single()
            assertEquals(1, opponent.wins)
            assertEquals(0, opponent.losses)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `error state is set when repository throws`() = runTest {
        every { entryRepository.getAllEntries() } returns flow { throw RuntimeException("db error") }
        val vm = createViewModel()

        vm.statsUIState.test {
            val state = awaitItem()
            assertNotNull(state.errorMessage)
            assertFalse(state.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
