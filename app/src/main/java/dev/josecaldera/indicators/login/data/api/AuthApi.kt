package dev.josecaldera.indicators.login.data.api

import dev.josecaldera.indicators.core.Result
import dev.josecaldera.indicators.core.map
import dev.josecaldera.indicators.login.domain.model.AuthError
import dev.josecaldera.indicators.login.domain.model.User

/**
 * Fake API. This ideally should be a retrofit interface
 */
class AuthApi(
    private val storage: UserStorage
) {

    init {
        storage.storeUsers(DEFAULT_USERS)
    }

    fun logIn(email: String, password: String): Result<User> {
        return storage.getUser(email)?.let {
            if (password == it.password) {
                Result.OnSuccess(it)
            } else {
                Result.OnError(AuthError)
            }
        }?.map { entity ->
            DEFAULT_USER_INFO.first { it.email == entity.email }
        } ?: Result.OnError(AuthError)
    }

    fun logOut() {
        // in a real world app, logout should be an API call to clear the user session token
    }

    companion object {
        private val DEFAULT_USERS = listOf(
            UserEntity("test1@test.com", "test1"),
            UserEntity("test2@test.com", "test2"),
            UserEntity("test3@test.com", "test3")
        )

        private val DEFAULT_USER_INFO = listOf(
            User("Juan", "test1@test.com"),
            User("Luis", "test2@test.com"),
            User("Maria", "test3@test.com")
        )
    }
}
