package dev.josecaldera.indicators.login.domain

import dev.josecaldera.indicators.core.Result
import dev.josecaldera.indicators.login.domain.model.User

interface AuthRepository {

    suspend fun logIn(request: AuthRequest): Result<User>

    suspend fun logOut()

    data class AuthRequest(
        val email: String,
        val password: String
    )
}
