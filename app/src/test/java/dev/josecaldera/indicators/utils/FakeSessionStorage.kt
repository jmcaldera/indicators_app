package dev.josecaldera.indicators.utils

import dev.josecaldera.indicators.core.Result
import dev.josecaldera.indicators.login.data.session.SessionStorage
import dev.josecaldera.indicators.login.domain.model.AuthError
import dev.josecaldera.indicators.login.domain.model.User

class FakeSessionStorage : SessionStorage {
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
