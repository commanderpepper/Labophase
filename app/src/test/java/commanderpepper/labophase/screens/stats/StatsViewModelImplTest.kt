package commanderpepper.labophase.screens.stats

import app.cash.turbine.test
import commanderpepper.labophase.data.EntryRepository
import commanderpepper.labophase.data.EntryWithRounds
import commanderpepper.labophase.data.OpponentRepository
import commanderpepper.labophase.data.entity.EntryEntity
import commanderpepper.labophase.data.entity.RoundEntity
import commanderpepper.labophase.models.Leader
import commanderpepper.labophase.models.Location
import commanderpepper.labophase.models.Meta
import commanderpepper.labophase.models.Opponent
import commanderpepper.labophase.models.RoundResult
import commanderpepper.labophase.models.TurnOrder
import commanderpepper.labophase.screens.stats.models.LeaderSelectedOption
import commanderpepper.labophase.screens.stats.models.LocationOption
import commanderpepper.labophase.screens.stats.models.MetaOption
import commanderpepper.labophase.screens.stats.models.StatsTab
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
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
class StatsViewModelImplTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val entryRepository: EntryRepository = mockk()
    private val opponentRepository: OpponentRepository = mockk()
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
        turnOrder: TurnOrder = TurnOrder.First,
        opponentId: Int? = null
    ) = RoundEntity(
        entryId = 1,
        roundNumber = 1,
        leaderCardId = opponentCardId,
        roundResult = result,
        turnOrder = turnOrder,
        opponentId = opponentId
    )

    private fun createViewModel() = StatsViewModelImpl(entryRepository, opponentRepository, testDispatcher)

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
            assertTrue(state.availableLocations.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `leaders played contains distinct leaders across all entries when no meta selected`() = runTest {
        val vm = createViewModel()

        vm.statsUIState.test {
            awaitItem()

            entriesFlow.value = listOf(
                makeEntry(id = 1, leaderCardId = Leader.UGLuffy.cardId, metaId = Meta.OP16.id),
                makeEntry(id = 2, leaderCardId = Leader.RShanks.cardId, metaId = Meta.OP16WithST.id),
                makeEntry(id = 3, leaderCardId = Leader.UGLuffy.cardId, metaId = Meta.OP16.id)
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

            assertEquals(66, state.leadersPlayerAgainst.single().percentage)
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
            assertEquals(50, opponent.firstPercentage)
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
            assertEquals(50, opponent.secondPercentage)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `turn order percentage is null when no rounds played in that turn order`() = runTest {
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
            assertEquals(100, opponent.firstPercentage)
            assertNull(opponent.secondPercentage)
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
            assertEquals(66, leaderState.leader.percentage)
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
            assertEquals(2, state.availableLocations.size)
            assertTrue(state.availableLocations.contains(Location.ChronosGamesAndGifts))
            assertTrue(state.availableLocations.contains(Location.TheClubhouse))
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
    fun `availableLocations contains distinct locations from entries`() = runTest {
        val vm = createViewModel()

        vm.statsUIState.test {
            awaitItem()

            entriesFlow.value = listOf(
                makeEntry(id = 1, locationId = Location.ChronosGamesAndGifts.id),
                makeEntry(id = 2, locationId = Location.TheClubhouse.id),
                makeEntry(id = 3, locationId = Location.ChronosGamesAndGifts.id)
            )
            val state = awaitItem()

            assertEquals(2, state.availableLocations.size)
            assertTrue(state.availableLocations.contains(Location.ChronosGamesAndGifts))
            assertTrue(state.availableLocations.contains(Location.TheClubhouse))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `availableLocations excludes entries with no location set`() = runTest {
        val vm = createViewModel()

        vm.statsUIState.test {
            awaitItem()

            entriesFlow.value = listOf(
                makeEntry(id = 1, locationId = Location.ChronosGamesAndGifts.id),
                makeEntry(id = 2, locationId = null)
            )
            val state = awaitItem()

            assertEquals(1, state.availableLocations.size)
            assertTrue(state.availableLocations.contains(Location.ChronosGamesAndGifts))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `availableLocations is scoped to selected meta`() = runTest {
        val vm = createViewModel()

        vm.statsUIState.test {
            awaitItem()

            entriesFlow.value = listOf(
                makeEntry(id = 1, metaId = Meta.OP16.id, locationId = Location.ChronosGamesAndGifts.id),
                makeEntry(id = 2, metaId = Meta.OP16WithST.id, locationId = Location.TheClubhouse.id)
            )
            awaitItem()

            vm.metaSelected(Meta.OP16)
            val state = awaitItem()

            assertEquals(1, state.availableLocations.size)
            assertTrue(state.availableLocations.contains(Location.ChronosGamesAndGifts))
            assertFalse(state.availableLocations.contains(Location.TheClubhouse))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `availableLocations shows all locations when meta cleared`() = runTest {
        val vm = createViewModel()

        vm.statsUIState.test {
            awaitItem()

            entriesFlow.value = listOf(
                makeEntry(id = 1, metaId = Meta.OP16.id, locationId = Location.ChronosGamesAndGifts.id),
                makeEntry(id = 2, metaId = Meta.OP16WithST.id, locationId = Location.TheClubhouse.id)
            )
            awaitItem()

            vm.metaSelected(Meta.OP16)
            awaitItem()

            vm.allMetaSelected()
            val state = awaitItem()

            assertEquals(2, state.availableLocations.size)
            assertTrue(state.availableLocations.contains(Location.ChronosGamesAndGifts))
            assertTrue(state.availableLocations.contains(Location.TheClubhouse))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `location selection is cleared when meta changes`() = runTest {
        val vm = createViewModel()

        vm.statsUIState.test {
            awaitItem()

            entriesFlow.value = listOf(
                makeEntry(id = 1, metaId = Meta.OP16.id, locationId = Location.ChronosGamesAndGifts.id, rounds = listOf(makeRound())),
                makeEntry(id = 2, metaId = Meta.OP16WithST.id, locationId = Location.TheClubhouse.id, rounds = listOf(makeRound()))
            )
            awaitItem()

            vm.locationSelected(Location.ChronosGamesAndGifts)
            awaitItem()

            vm.metaSelected(Meta.OP16WithST)
            val state = awaitItem()

            assertTrue(state.locationSelected is LocationOption.All)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `leadersPlayed is scoped to selected meta`() = runTest {
        val vm = createViewModel()

        vm.statsUIState.test {
            awaitItem()

            entriesFlow.value = listOf(
                makeEntry(id = 1, leaderCardId = Leader.UGLuffy.cardId, metaId = Meta.OP16.id),
                makeEntry(id = 2, leaderCardId = Leader.RShanks.cardId, metaId = Meta.OP16WithST.id)
            )
            awaitItem()

            vm.metaSelected(Meta.OP16)
            val state = awaitItem()

            assertEquals(1, state.leadersPlayed.size)
            assertTrue(state.leadersPlayed.contains(Leader.UGLuffy))
            assertFalse(state.leadersPlayed.contains(Leader.RShanks))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `leader selection falls back to all when selected leader not played in new meta`() = runTest {
        val vm = createViewModel()

        vm.statsUIState.test {
            awaitItem()

            entriesFlow.value = listOf(
                makeEntry(id = 1, leaderCardId = Leader.UGLuffy.cardId, metaId = Meta.OP16.id, rounds = listOf(makeRound())),
                makeEntry(id = 2, leaderCardId = Leader.RShanks.cardId, metaId = Meta.OP16WithST.id, rounds = listOf(makeRound()))
            )
            awaitItem()

            vm.leaderSelected(Leader.UGLuffy)
            awaitItem()

            vm.metaSelected(Meta.OP16WithST)
            val state = awaitItem()

            assertTrue(state.leaderSelected is LeaderSelectedOption.All)
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

    // region — Opponent stats

    @Test
    fun `opponentStats includes all opponents including those with no games`() = runTest {
        opponentsFlow.value = listOf(Opponent(1, "playerA", "A"), Opponent(2, "playerB", "B"))
        entriesFlow.value = listOf(makeEntry(rounds = listOf(makeRound(opponentId = 1))))
        val vm = createViewModel()

        vm.statsUIState.test {
            val state = awaitItem()
            assertEquals(2, state.opponentStats.size)
            assertTrue(state.opponentStats.any { it.opponent.id == 1 })
            assertTrue(state.opponentStats.any { it.opponent.id == 2 })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `opponent with zero games has zero wins losses and null percentage`() = runTest {
        opponentsFlow.value = listOf(Opponent(1, "playerA", "A"))
        val vm = createViewModel()

        vm.statsUIState.test {
            val state = awaitItem()
            val stat = state.opponentStats.single()
            assertEquals(0, stat.wins)
            assertEquals(0, stat.losses)
            assertNull(stat.percentage)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `opponentStats are sorted by total play count descending`() = runTest {
        opponentsFlow.value = listOf(
            Opponent(1, "playerA", "A"),
            Opponent(2, "playerB", "B"),
            Opponent(3, "playerC", "C")
        )
        entriesFlow.value = listOf(
            makeEntry(rounds = listOf(
                makeRound(opponentId = 2),
                makeRound(opponentId = 2),
                makeRound(opponentId = 2),
                makeRound(opponentId = 1)
            ))
        )
        val vm = createViewModel()

        vm.statsUIState.test {
            val state = awaitItem()
            assertEquals(2, state.opponentStats[0].opponent.id)
            assertEquals(1, state.opponentStats[1].opponent.id)
            assertEquals(3, state.opponentStats[2].opponent.id)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `opponents with zero games appear after those with games`() = runTest {
        opponentsFlow.value = listOf(Opponent(1, "playerA", "A"), Opponent(2, "playerB", "B"))
        entriesFlow.value = listOf(makeEntry(rounds = listOf(makeRound(opponentId = 2))))
        val vm = createViewModel()

        vm.statsUIState.test {
            val state = awaitItem()
            assertEquals(2, state.opponentStats[0].opponent.id)
            assertEquals(1, state.opponentStats[1].opponent.id)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `opponent win and loss counts are tracked correctly`() = runTest {
        opponentsFlow.value = listOf(Opponent(1, "playerA", "A"))
        entriesFlow.value = listOf(
            makeEntry(rounds = listOf(
                makeRound(opponentId = 1, result = RoundResult.Win),
                makeRound(opponentId = 1, result = RoundResult.Win),
                makeRound(opponentId = 1, result = RoundResult.Loss)
            ))
        )
        val vm = createViewModel()

        vm.statsUIState.test {
            val state = awaitItem()
            val stat = state.opponentStats.single()
            assertEquals(2, stat.wins)
            assertEquals(1, stat.losses)
            assertEquals(66, stat.percentage)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `opponent stats are computed independently per opponent`() = runTest {
        opponentsFlow.value = listOf(Opponent(1, "playerA", "A"), Opponent(2, "playerB", "B"))
        entriesFlow.value = listOf(
            makeEntry(rounds = listOf(
                makeRound(opponentId = 1, result = RoundResult.Win),
                makeRound(opponentId = 2, result = RoundResult.Loss),
                makeRound(opponentId = 2, result = RoundResult.Loss)
            ))
        )
        val vm = createViewModel()

        vm.statsUIState.test {
            val state = awaitItem()
            val statA = state.opponentStats.first { it.opponent.id == 1 }
            val statB = state.opponentStats.first { it.opponent.id == 2 }
            assertEquals(1, statA.wins)
            assertEquals(0, statA.losses)
            assertEquals(0, statB.wins)
            assertEquals(2, statB.losses)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `rounds without opponentId are not attributed to any opponent`() = runTest {
        opponentsFlow.value = listOf(Opponent(1, "playerA", "A"))
        entriesFlow.value = listOf(
            makeEntry(rounds = listOf(
                makeRound(opponentId = null, result = RoundResult.Win),
                makeRound(opponentId = null, result = RoundResult.Loss)
            ))
        )
        val vm = createViewModel()

        vm.statsUIState.test {
            val state = awaitItem()
            val stat = state.opponentStats.single()
            assertEquals(0, stat.wins)
            assertEquals(0, stat.losses)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // endregion

    @Test
    fun `opponent stats respect meta filter`() = runTest {
        opponentsFlow.value = listOf(Opponent(1, "playerA", "A"))
        entriesFlow.value = listOf(
            makeEntry(metaId = Meta.OP16.id, rounds = listOf(makeRound(opponentId = 1, result = RoundResult.Win))),
            makeEntry(metaId = Meta.OP16WithST.id, rounds = listOf(makeRound(opponentId = 1, result = RoundResult.Loss)))
        )
        val vm = createViewModel()

        vm.statsUIState.test {
            awaitItem()
            vm.metaSelected(Meta.OP16)
            val state = awaitItem()
            val stat = state.opponentStats.single()
            assertEquals(1, stat.wins)
            assertEquals(0, stat.losses)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `opponent stats respect leader filter`() = runTest {
        opponentsFlow.value = listOf(Opponent(1, "playerA", "A"))
        entriesFlow.value = listOf(
            makeEntry(leaderCardId = Leader.UGLuffy.cardId, rounds = listOf(makeRound(opponentId = 1, result = RoundResult.Win))),
            makeEntry(leaderCardId = Leader.RShanks.cardId, rounds = listOf(makeRound(opponentId = 1, result = RoundResult.Loss)))
        )
        val vm = createViewModel()

        vm.statsUIState.test {
            awaitItem()
            vm.leaderSelected(Leader.UGLuffy)
            val state = awaitItem()
            val stat = state.opponentStats.single()
            assertEquals(1, stat.wins)
            assertEquals(0, stat.losses)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `opponent stats respect location filter`() = runTest {
        opponentsFlow.value = listOf(Opponent(1, "playerA", "A"))
        entriesFlow.value = listOf(
            makeEntry(locationId = Location.ChronosGamesAndGifts.id, rounds = listOf(makeRound(opponentId = 1, result = RoundResult.Win))),
            makeEntry(locationId = Location.TheClubhouse.id, rounds = listOf(makeRound(opponentId = 1, result = RoundResult.Loss)))
        )
        val vm = createViewModel()

        vm.statsUIState.test {
            awaitItem()
            vm.locationSelected(Location.ChronosGamesAndGifts)
            val state = awaitItem()
            val stat = state.opponentStats.single()
            assertEquals(1, stat.wins)
            assertEquals(0, stat.losses)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // region — Tab selection

    @Test
    fun `default active tab is Leaders`() = runTest {
        val vm = createViewModel()

        vm.statsUIState.test {
            val state = awaitItem()
            assertEquals(StatsTab.Leaders, state.activeTab)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `tabSelected Opponents changes activeTab to Opponents`() = runTest {
        val vm = createViewModel()

        vm.statsUIState.test {
            awaitItem()
            vm.tabSelected(StatsTab.Opponents)
            val state = awaitItem()
            assertEquals(StatsTab.Opponents, state.activeTab)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `tabSelected Leaders changes activeTab back to Leaders`() = runTest {
        val vm = createViewModel()

        vm.statsUIState.test {
            awaitItem()
            vm.tabSelected(StatsTab.Opponents)
            awaitItem()
            vm.tabSelected(StatsTab.Leaders)
            val state = awaitItem()
            assertEquals(StatsTab.Leaders, state.activeTab)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // endregion

    // region — Repository delegation

    @Test
    fun `addOpponent delegates to repository with correct arguments`() = runTest {
        coEvery { opponentRepository.addOpponent(any(), any()) } returns Unit
        val vm = createViewModel()
        vm.addOpponent("newhandle", "NewNick")
        advanceUntilIdle()
        coVerify { opponentRepository.addOpponent("newhandle", "NewNick") }
    }

    @Test
    fun `updateOpponent delegates to repository with correct arguments`() = runTest {
        coEvery { opponentRepository.updateOpponent(any(), any(), any()) } returns Unit
        val vm = createViewModel()
        vm.updateOpponent(7, "updatedhandle", "UpdatedNick")
        advanceUntilIdle()
        coVerify { opponentRepository.updateOpponent(7, "updatedhandle", "UpdatedNick") }
    }

    @Test
    fun `deleteOpponent delegates to repository with correct id`() = runTest {
        coEvery { opponentRepository.deleteOpponent(any()) } returns Unit
        val vm = createViewModel()
        vm.deleteOpponent(3)
        advanceUntilIdle()
        coVerify { opponentRepository.deleteOpponent(3) }
    }

    // endregion
}
