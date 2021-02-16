package dev.josecaldera.indicators.login.data.session

import dev.josecaldera.indicators.core.Result
import dev.josecaldera.indicators.login.domain.model.AuthError
import dev.josecaldera.indicators.login.domain.model.User
import dev.josecaldera.indicators.storage.Storage

interface SessionStorage {

    fun getUser(): Result<User>

    fun saveUser(user: User)

    fun clear()
}

class LocalSessionStorage(
    storageFactory: Storage.Factory
) : SessionStorage {

    companion object {
        private const val FILE_NAME = "session_storage"
        private const val KEY_NAME = "name"
        private const val KEY_EMAIL = "email"
    }

    private val storage = storageFactory.create(FILE_NAME)

    override fun getUser(): Result<User> {
        val name = storage.getString(KEY_NAME)
        val email = storage.getString(KEY_EMAIL)

        // maybe a different error could be used here
        return if (name == null && email == null) Result.OnError(AuthError)
        else Result.OnSuccess(User(name!!, email!!))
    }

    override fun saveUser(user: User) {
        storage.putString(KEY_NAME, user.name)
        storage.putString(KEY_EMAIL, user.email)
    }

    override fun clear() {
        storage.clear()
    }
}
