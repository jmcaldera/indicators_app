package dev.josecaldera.indicators.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import dev.josecaldera.indicators.core.Result
import dev.josecaldera.indicators.login.domain.AuthRepository
import dev.josecaldera.indicators.login.domain.model.AuthError
import dev.josecaldera.indicators.login.domain.model.User
import dev.josecaldera.indicators.login.ui.LoginViewModel
import dev.josecaldera.indicators.utils.CoroutinesTestRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    var coroutinesTestRule = CoroutinesTestRule()

    private val authRepository = mockk<AuthRepository>(relaxed = true)

    @Before
    fun setUp() {
    }

    @Test
    fun `GIVEN viewModel WHEN init THEN values are empty`() {
        createViewModel().whileObserving {
            assertTrue(email.value!!.isEmpty())
            assertTrue(emailError.value!!.isEmpty())
            assertTrue(password.value!!.isEmpty())
        }
    }

    @Test
    fun `GIVEN viewModel WHEN invalid email THEN emailError is not empty`() {
        createViewModel().whileObserving {
            email.value = "email"

            assertTrue(emailError.value!!.isNotBlank())
        }
    }

    @Test
    fun `GIVEN viewModel WHEN valid email THEN emailError is empty`() {
        createViewModel().whileObserving {
            email.value = "email@email.com"

            assertTrue(emailError.value!!.isEmpty())
        }
    }

    @Test
    fun `GIVEN viewModel WHEN valid password and email THEN button is enabled`() {
        createViewModel().whileObserving {
            email.value = "email@email.com"
            password.value = "123456"

            assertTrue(isLogInButtonEnabled.value!!)
        }
    }

    @Test
    fun `GIVEN viewModel WHEN invalid password and email THEN button is disabled`() {
        createViewModel().whileObserving {
            email.value = "email"
            password.value = ""

            assertFalse(isLogInButtonEnabled.value!!)
        }
    }

    @Test
    fun `GIVEN viewModel WHEN password focus lost THEN send HideSoftKeyboard event`() =
        runBlockingTest {
            createViewModel().whileObserving {

                val eventList = mutableListOf<LoginViewModel.Event>()

                val job = launch(coroutineContext) {
                    events.collect {
                        eventList.add(it)
                    }
                }

                password.value = "1234"

                onPasswordFocusChanged(false)

                assertTrue(eventList.isNotEmpty())
                assertEquals(LoginViewModel.Event.HideSoftKeyboard, eventList.first())

                job.cancel()
            }
        }

    @Test
    fun `GIVEN valid credentials WHEN onLoginClicked THEN call repository`() =
        coroutinesTestRule.runBlockingTest {
            createViewModel().whileObserving {
                email.value = "email@email.com"
                password.value = "123456"

                val request = AuthRepository.AuthRequest("email@email.com", "123456")
                onLoginClicked()

                coVerify { authRepository.logIn(request) }
            }
        }

    @Test
    fun `GIVEN valid credentials WHEN onLoginClicked THEN send HideSoftkeyboard event`() =
        coroutinesTestRule.runBlockingTest {
            createViewModel().whileObserving {
                email.value = "email@email.com"
                password.value = "123456"

                val eventList = mutableListOf<LoginViewModel.Event>()

                val job = launch(coroutineContext) {
                    events.collect {
                        eventList.add(it)
                    }
                }

                onLoginClicked()

                assertTrue(eventList.isNotEmpty())
                assertEquals(LoginViewModel.Event.HideSoftKeyboard, eventList.first())

                job.cancel()
            }
        }

    @Test
    fun `GIVEN login request WHEN success THEN send LoginSuccess event`() =
        coroutinesTestRule.runBlockingTest {
            createViewModel().whileObserving {
                val user = User("name", "email@email.com")
                coEvery { authRepository.logIn(any()) } returns Result.OnSuccess(user)

                val eventList = mutableListOf<LoginViewModel.Event>()

                val job = launch(coroutineContext) {
                    events.collect {
                        eventList.add(it)
                    }
                }

                email.value = "email@email.com"
                password.value = "123456"

                onLoginClicked()

                assertTrue(eventList.isNotEmpty())
                val event = eventList[1] as LoginViewModel.Event.LoginSuccess
                assertEquals(user, event.user)

                job.cancel()
            }
        }

    @Test
    fun `GIVEN login request WHEN error THEN send LoginError event`() =
        coroutinesTestRule.runBlockingTest {
            createViewModel().whileObserving {
                coEvery { authRepository.logIn(any()) } returns Result.OnError(AuthError)

                val eventList = mutableListOf<LoginViewModel.Event>()

                val job = launch(coroutineContext) {
                    events.collect {
                        eventList.add(it)
                    }
                }

                email.value = "email@email.com"
                password.value = "123456"

                onLoginClicked()

                assertTrue(eventList.isNotEmpty())
                assertTrue(eventList[1] is LoginViewModel.Event.LoginError)

                job.cancel()
            }
        }

    private fun createViewModel(
        savedStateHandle: SavedStateHandle = SavedStateHandle()
    ): LoginViewModel {
        return LoginViewModel(
            savedStateHandle,
            authRepository
        )
    }

    private fun LoginViewModel.whileObserving(block: LoginViewModel.() -> Unit) {
        val stringObserver: Observer<String> = Observer { }
        val booleanObserver: Observer<Boolean> = Observer { }


        email.observeForever(stringObserver)
        emailError.observeForever(stringObserver)
        password.observeForever(stringObserver)
        isLoading.observeForever(booleanObserver)
        isLogInButtonEnabled.observeForever(booleanObserver)

        block(this)

        email.removeObserver(stringObserver)
        emailError.removeObserver(stringObserver)
        password.removeObserver(stringObserver)
        isLoading.removeObserver(booleanObserver)
        isLogInButtonEnabled.removeObserver(booleanObserver)
    }
}
