package dev.josecaldera.indicators.login.data.session

import dev.josecaldera.indicators.login.domain.model.User
import dev.josecaldera.indicators.storage.FakeStorageFactory
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class LocalSessionStorageTest {

    private val factory = FakeStorageFactory()
    private lateinit var storage: LocalSessionStorage

    @Before
    fun setUp() {
        storage = LocalSessionStorage(factory)
    }

    @After
    fun tearDown() {
        factory.storage.clear()
    }

    @Test
    fun `GIVEN user WHEN saveUser THEN save user`() {
        val user = User("name", "email")

        storage.saveUser(user)

        verify {
            factory.storage.putString("name", user.name)
            factory.storage.putString("email", user.email)
        }
    }

    @Test
    fun `GIVEN stored user WHEN getUser THEN return success`() {
        val user = User("name", "email")

        storage.saveUser(user)

        assertTrue(factory.storage.data.isNotEmpty())

        val result = storage.getUser()

        assertTrue(result.isSuccess())
    }

    @Test
    fun `GIVEN stored user WHEN getUser THEN return user`() {
        val user = User("name", "email")

        storage.saveUser(user)

        assertTrue(factory.storage.data.isNotEmpty())

        val savedUser = storage.getUser().getOrNull()

        assertEquals(user, savedUser)
    }

    @Test
    fun `GIVEN empty storage WHEN getUser THEN return error`() {
        val result = storage.getUser()

        assertTrue(result.isError())
    }

    @Test
    fun `GIVEN stoage WHEN clear THEN clear storage`() {
        storage.clear()

        verify { factory.storage.clear() }
    }
}
