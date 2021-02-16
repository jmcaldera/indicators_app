package dev.josecaldera.indicators.login.data

import dev.josecaldera.indicators.core.Result
import dev.josecaldera.indicators.login.data.api.AuthApi
import dev.josecaldera.indicators.login.data.session.SessionStorage
import dev.josecaldera.indicators.login.domain.AuthRepository
import dev.josecaldera.indicators.login.domain.model.AuthError
import dev.josecaldera.indicators.login.domain.model.User
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class LocalAuthRepositoryTest {

    // we can use mocks for testing
    private val authApi = mockk<AuthApi>(relaxed = true)

    // But we can also use fakes
    private val sessionStorage = spyk(FakeSessionStorage())

    private lateinit var repository: LocalAuthRepository

    @Before
    fun setUp() {
        repository = LocalAuthRepository(
            authApi = authApi,
            sessionStorage = sessionStorage,
            ioDispatcher = Dispatchers.Unconfined
        )
    }

    @After
    fun tearDown() {
        sessionStorage.clear()
    }

    @Test
    fun `GIVEN request WHEN logIn THEN call api`() = runBlockingTest {
        val request = AuthRepository.AuthRequest("email", "password")

        repository.logIn(request)

        verify { authApi.logIn(request.email, request.password) }
    }

    @Test
    fun `GIVEN login WHEN success THEN save user`() = runBlockingTest {
        val request = AuthRepository.AuthRequest("email", "password")

        val user = User("name", "email")

        every { authApi.logIn(request.email, request.password) } returns Result.OnSuccess(user)

        // no user present before login
        assertTrue(sessionStorage.getUser().isError())

        repository.logIn(request)

        verify { sessionStorage.saveUser(user) }

        val savedUser = sessionStorage.getUser().getOrNull()
        assertEquals(user, savedUser)
    }

    @Test
    fun `GIVEN login WHEN success THEN return success`() = runBlockingTest {
        val request = AuthRepository.AuthRequest("email", "password")

        val user = User("name", "email")

        every { authApi.logIn(request.email, request.password) } returns Result.OnSuccess(user)

        val result = repository.logIn(request)

        assertTrue(result.isSuccess())
    }

    @Test
    fun `GIVEN login WHEN error THEN user is not saved`() = runBlockingTest {
        val request = AuthRepository.AuthRequest("email", "password")

        every { authApi.logIn(request.email, request.password) } returns Result.OnError(AuthError)

        // no user present before login
        assertTrue(sessionStorage.getUser().isError())

        repository.logIn(request)

        // not called
        verify(inverse = true) { sessionStorage.saveUser(any()) }

        // session is still empty
        assertTrue(sessionStorage.getUser().isError())
    }

    @Test
    fun `GIVEN login WHEN success THEN return error`() = runBlockingTest {
        val request = AuthRepository.AuthRequest("email", "password")

        every { authApi.logIn(request.email, request.password) } returns Result.OnError(AuthError)

        val result = repository.logIn(request)

        assertTrue(result.isError())
    }

    @Test
    fun `GIVEN non empty session WHEN logout THEN clear session`() = runBlockingTest {
        val user = User("name", "email")
        sessionStorage.saveUser(user)

        repository.logOut()

        verify { sessionStorage.clear() }

        assertTrue(sessionStorage.getUser().isError())
    }

    @Test
    fun `GIVEN non empty session WHEN logout THEN call api`() = runBlockingTest {
        val user = User("name", "email")
        sessionStorage.saveUser(user)

        repository.logOut()

        verify { authApi.logOut() }
    }

    private class FakeSessionStorage : SessionStorage {
        private var user: User? = null

        override fun getUser(): Result<User> {
            return user?.let {
                Result.OnSuccess(it)
            } ?: Result.OnError(AuthError)
        }

        override fun saveUser(user: User) {
            this.user = user
        }

        override fun clear() {
            user = null
        }
    }
}
