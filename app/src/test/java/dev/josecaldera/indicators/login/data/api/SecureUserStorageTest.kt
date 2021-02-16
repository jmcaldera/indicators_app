package dev.josecaldera.indicators.login.data.api

import dev.josecaldera.indicators.storage.FakeStorageFactory
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue


class SecureUserStorageTest {

    private val factory = FakeStorageFactory()
    private lateinit var secureStorage: SecureUserStorage

    @Before
    fun setUp() {
        secureStorage = SecureUserStorage(factory)
    }

    @After
    fun tearDown() {
        factory.storage.clear()
    }

    @Test
    fun `GIVEN users WHEN storeUsers THEN save users`() {
        val users = listOf(
            UserEntity("email@email.com", "password")
        )

        secureStorage.storeUsers(users)

        verify { factory.storage.putString("email@email.com", "password") }
        assertTrue(factory.storage.data.isNotEmpty())
    }

    @Test
    fun `GIVEN stored user WHEN getUser THEN return user`() {
        val user = UserEntity("email@email.com", "password")
        val users = listOf(user)

        secureStorage.storeUsers(users)
        assertTrue(factory.storage.data.isNotEmpty())


        val savedUser = secureStorage.getUser("email@email.com")

        verify { factory.storage.getString("email@email.com") }
        assertEquals(user, savedUser)
    }

    @Test
    fun `GIVEN empty storage WHEN getUser THEN return null`() {
        assertTrue(factory.storage.data.isEmpty())
        val savedUser = secureStorage.getUser("email@email.com")

        verify { factory.storage.getString("email@email.com") }
        assertNull(savedUser)
    }
}