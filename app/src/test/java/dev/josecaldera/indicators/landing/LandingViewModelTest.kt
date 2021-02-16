package dev.josecaldera.indicators.landing

import dev.josecaldera.indicators.login.domain.model.User
import dev.josecaldera.indicators.main.ui.IndicatorsViewModel
import dev.josecaldera.indicators.utils.CoroutinesTestRule
import dev.josecaldera.indicators.utils.FakeSessionStorage
import io.mockk.spyk
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertTrue


class LandingViewModelTest {

    @Rule
    @JvmField
    var coroutinesTestRule = CoroutinesTestRule()

    private val sessionStorage = spyk(FakeSessionStorage())
    private lateinit var viewModel: LandingViewModel

    @Before
    fun setUp() {
        viewModel = LandingViewModel(sessionStorage)
    }

    @After
    fun tearDown() {
        sessionStorage.clear()
    }

    @Test
    fun `GIVEN empty storage WHEN checkUserStatus THEN send NavigateToLogin event`() =
        coroutinesTestRule.runBlockingTest {
            sessionStorage.clear()

            val eventList = mutableListOf<LandingViewModel.Event>()

            val job = launch(coroutineContext) {
                viewModel.events.collect {
                    eventList.add(it)
                }
            }

            viewModel.checkUserStatus()

            assertTrue(eventList.isNotEmpty())
            assertTrue(eventList.first() is LandingViewModel.Event.NavigateToLogin)

            job.cancel()
        }

    @Test
    fun `GIVEN non empty storage WHEN checkUserStatus THEN send NavigateToIndicators event`() =
        coroutinesTestRule.runBlockingTest {

            sessionStorage.saveUser(User("name", "email"))

            val eventList = mutableListOf<LandingViewModel.Event>()

            val job = launch(coroutineContext) {
                viewModel.events.collect {
                    eventList.add(it)
                }
            }

            viewModel.checkUserStatus()

            assertTrue(eventList.isNotEmpty())
            assertTrue(eventList.first() is LandingViewModel.Event.NavigateToIndicators)
            job.cancel()
        }
}
