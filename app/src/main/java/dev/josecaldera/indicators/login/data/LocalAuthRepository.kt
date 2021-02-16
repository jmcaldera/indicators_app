package dev.josecaldera.indicators.login.data

import dev.josecaldera.indicators.core.Result
import dev.josecaldera.indicators.core.onSuccess
import dev.josecaldera.indicators.login.data.api.AuthApi
import dev.josecaldera.indicators.login.data.session.SessionStorage
import dev.josecaldera.indicators.login.domain.AuthRepository
import dev.josecaldera.indicators.login.domain.model.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.contracts.ExperimentalContracts

@OptIn(ExperimentalContracts::class)
class LocalAuthRepository(
    private val authApi: AuthApi,
    private val sessionStorage: SessionStorage,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AuthRepository {

    override suspend fun logIn(request: AuthRepository.AuthRequest): Result<User> {
        // remote operations should be performed on a background thread
        return withContext(ioDispatcher) {
            authApi.logIn(request.email, request.password)
                .onSuccess {
                    // Save to prefs
                    sessionStorage.saveUser(it)
                }
        }
    }

    override suspend fun logOut() {
        withContext(ioDispatcher) {
            authApi.logOut()
            sessionStorage.clear()
        }
    }
}
