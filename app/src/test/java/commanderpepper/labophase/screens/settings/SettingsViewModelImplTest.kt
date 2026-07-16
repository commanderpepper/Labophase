package commanderpepper.labophase.screens.settings

import commanderpepper.labophase.data.SettingsRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelImplTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val settingsRepository: SettingsRepository = mockk(relaxed = true)

    private val showAllLeadersFlow = MutableStateFlow(false)
    private val showDieRollFlow = MutableStateFlow(false)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { settingsRepository.showingAllLeaders() } returns showAllLeadersFlow
        every { settingsRepository.showingDieRoll() } returns showDieRollFlow
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = SettingsViewModelImpl(settingsRepository)

    @Test
    fun `showingAllLeaders initial value is false`() = runTest {
        val vm = createViewModel()
        assertEquals(false, vm.showingAllLeaders.value)
    }

    @Test
    fun `showingAllLeaders reflects repository flow`() = runTest {
        val vm = createViewModel()

        showAllLeadersFlow.value = false

        assertEquals(false, vm.showingAllLeaders.value)
    }

    @Test
    fun `showingDieRoll initial value is false`() = runTest {
        val vm = createViewModel()
        assertEquals(false, vm.showingDieRoll.value)
    }

    @Test
    fun `showingDieRoll reflects repository flow`() = runTest {
        val vm = createViewModel()

        showDieRollFlow.value = false

        assertEquals(false, vm.showingDieRoll.value)
    }

    @Test
    fun `toggleLeaders delegates to repository`() = runTest {
        val vm = createViewModel()

        vm.toggleLeaders()
        advanceUntilIdle()

        coVerify(exactly = 1) { settingsRepository.toggleLeadersShown() }
    }

    @Test
    fun `toggleDieRoll delegates to repository`() = runTest {
        val vm = createViewModel()

        vm.toggleDieRoll()
        advanceUntilIdle()

        coVerify(exactly = 1) { settingsRepository.toggleDieRoll() }
    }

    @Test
    fun `deleteHistory delegates to repository`() = runTest {
        val vm = createViewModel()

        vm.deleteHistory()
        advanceUntilIdle()

        coVerify(exactly = 1) { settingsRepository.clearHistory() }
    }
}
