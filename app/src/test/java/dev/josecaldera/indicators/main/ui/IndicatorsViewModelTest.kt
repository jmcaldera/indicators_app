package dev.josecaldera.indicators.main.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import dev.josecaldera.indicators.args.toParcelable
import dev.josecaldera.indicators.core.Result
import dev.josecaldera.indicators.login.domain.AuthRepository
import dev.josecaldera.indicators.login.domain.model.User
import dev.josecaldera.indicators.main.domain.IndicatorsRepository
import dev.josecaldera.indicators.main.domain.model.Indicator
import dev.josecaldera.indicators.main.domain.model.IndicatorsError
import dev.josecaldera.indicators.main.ui.adapter.RecyclerViewItem
import dev.josecaldera.indicators.utils.CoroutinesTestRule
import dev.josecaldera.indicators.utils.FakeSessionStorage
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class IndicatorsViewModelTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    var coroutinesTestRule = CoroutinesTestRule()

    private val authRepository = mockk<AuthRepository>(relaxed = true)
    private val indicatorsRepository = mockk<IndicatorsRepository>(relaxed = true)
    private val sessionStorage = spyk(FakeSessionStorage())

    @Before
    fun setUp() {
        sessionStorage.saveUser(User("name", "email"))
    }

    @After
    fun tearDown() {
        sessionStorage.clear()
    }

    @Test
    fun `GIVEN viewModel WHEN fetchIndicators() THEN fetch indicators`() {
        createViewModel().whileObserving {
            fetchIndicators()
            coVerify { indicatorsRepository.getIndicators() }
        }
    }

    @Test
    fun `GIVEN repository call WHEN success THEN update items list`() {
        val indicator = fakeIndicator()
        coEvery { indicatorsRepository.getIndicators() } returns
                Result.OnSuccess(listOf(indicator))

        createViewModel().whileObserving {

            fetchIndicators()

            assertEquals(2, items.value?.size)
        }
    }

    @Test
    fun `GIVEN repository call WHEN failure THEN send Failure event`() =
        coroutinesTestRule.runBlockingTest {

            coEvery { indicatorsRepository.getIndicators() } returns
                    Result.OnError(IndicatorsError(""))

            createViewModel().whileObserving {

                val eventList = mutableListOf<IndicatorsViewModel.Event>()

                val job = launch(coroutineContext) {
                    events.collect {
                        eventList.add(it)
                    }
                }

                fetchIndicators()

                assertTrue(eventList.isNotEmpty())
                assertTrue(eventList.first() is IndicatorsViewModel.Event.Failure)

                job.cancel()
            }
        }

    @Test
    fun `GIVEN failure event WHEN onRetry THEN call repository`() =
        coroutinesTestRule.runBlockingTest {

            coEvery { indicatorsRepository.getIndicators() } returns
                    Result.OnError(IndicatorsError(""))

            createViewModel().whileObserving {

                val eventList = mutableListOf<IndicatorsViewModel.Event>()

                val job = launch(coroutineContext) {
                    events.collect {
                        eventList.add(it)
                    }
                }

                fetchIndicators()

                assertTrue(eventList.isNotEmpty())
                val event = eventList.first() as IndicatorsViewModel.Event.Failure

                event.onRetry.invoke()

                coVerify(exactly = 2) {
                    indicatorsRepository.getIndicators()
                }

                job.cancel()
            }
        }

    @Test
    fun `GIVEN indicator item WHEN onIndicatorClicked THEN send NavigateToDetails event`() =
        coroutinesTestRule.runBlockingTest {
            val indicator = fakeIndicator()
            coEvery { indicatorsRepository.getIndicators() } returns
                    Result.OnSuccess(listOf(indicator))

            createViewModel().whileObserving {

                val eventList = mutableListOf<IndicatorsViewModel.Event>()

                val job = launch(coroutineContext) {
                    events.collect {
                        eventList.add(it)
                    }
                }

                fetchIndicators()

                items.value?.first()?.let {
                    (it as IndicatorItem).onClick.invoke(it.indicator)
                }

                assertTrue(eventList.isNotEmpty())
                val event = eventList.first() as IndicatorsViewModel.Event.NavigateToDetails

                assertEquals(indicator.toParcelable(), event.indicator)

                job.cancel()
            }
        }

    @Test
    fun `GIVEN viewModel WHEN onLogoutClicked THEN call repository`() =
        coroutinesTestRule.runBlockingTest {

            val indicator = fakeIndicator()
            coEvery { indicatorsRepository.getIndicators() } returns
                    Result.OnSuccess(listOf(indicator))

            createViewModel().whileObserving {

                fetchIndicators()

                items.value?.last()?.let {
                    (it as LogoutItem).onClick.invoke()
                }

                coVerify { authRepository.logOut() }
            }
        }

    @Test
    fun `GIVEN viewModel WHEN onLogoutClicked THEN send Logout event`() =
        coroutinesTestRule.runBlockingTest {

            val indicator = fakeIndicator()
            coEvery { indicatorsRepository.getIndicators() } returns
                    Result.OnSuccess(listOf(indicator))

            createViewModel().whileObserving {

                val eventList = mutableListOf<IndicatorsViewModel.Event>()

                val job = launch(coroutineContext) {
                    events.collect {
                        eventList.add(it)
                    }
                }

                fetchIndicators()

                items.value?.last()?.let {
                    (it as LogoutItem).onClick.invoke()
                }

                assertTrue(eventList.isNotEmpty())
                assertTrue(eventList.first() is IndicatorsViewModel.Event.Logout)

                job.cancel()
            }
        }

    @Test
    fun `GIVEN viewModel WHEN init THEN get user session`() {
        createViewModel().whileObserving {
            verify { sessionStorage.getUser() }
        }
    }

    @Test(expected = IllegalStateException::class)
    fun `GIVEN empty user session WHEN init THEN throw exception`() {
        sessionStorage.clear()
        createViewModel().whileObserving {
            verify { sessionStorage.getUser() }
        }
    }

    private fun createViewModel(
        savedStateHandle: SavedStateHandle = SavedStateHandle()
    ): IndicatorsViewModel {
        return IndicatorsViewModel(
            savedStateHandle,
            indicatorsRepository,
            authRepository,
            sessionStorage
        )
    }

    private fun IndicatorsViewModel.whileObserving(block: IndicatorsViewModel.() -> Unit) {
        val itemsObserver: Observer<List<RecyclerViewItem>> = Observer { }
        val booleanObserver: Observer<Boolean> = Observer { }

        items.observeForever(itemsObserver)
        isLoading.observeForever(booleanObserver)

        block(this)

        items.removeObserver(itemsObserver)
        isLoading.removeObserver(booleanObserver)
    }

    private fun fakeIndicator(): Indicator {
        return Indicator(
            code = "",
            name = "",
            unit = Indicator.UnitType.DOLLAR,
            date = "",
            value = ""
        )
    }
}