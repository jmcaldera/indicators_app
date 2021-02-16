package dev.josecaldera.indicators.di

import dev.josecaldera.indicators.login.data.LocalAuthRepository
import dev.josecaldera.indicators.login.data.api.AuthApi
import dev.josecaldera.indicators.login.data.api.SecureUserStorage
import dev.josecaldera.indicators.login.data.api.UserStorage
import dev.josecaldera.indicators.login.data.session.LocalSessionStorage
import dev.josecaldera.indicators.login.data.session.SessionStorage
import dev.josecaldera.indicators.login.domain.AuthRepository
import dev.josecaldera.indicators.login.ui.LoginViewModel
import dev.josecaldera.indicators.storage.SecureStorageFactory
import dev.josecaldera.indicators.storage.Storage
import dev.josecaldera.indicators.ui.IndicatorsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val applicationModule = module {

    // ViewModels
    viewModel { IndicatorsViewModel(get()) }
    viewModel { LoginViewModel(get(), get()) }

    // Storage
    factory<Storage.Factory> { SecureStorageFactory(androidContext()) }
    single<UserStorage> { SecureUserStorage(get()) }
    single<SessionStorage> { LocalSessionStorage(get()) }

    // API
    single { AuthApi(get()) }

    // Repositories
    single<AuthRepository> { LocalAuthRepository(get(), get()) }
}
