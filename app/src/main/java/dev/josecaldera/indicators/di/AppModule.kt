package dev.josecaldera.indicators.di

import com.squareup.moshi.Moshi
import dev.josecaldera.indicators.details.IndicatorDetailsViewModel
import dev.josecaldera.indicators.login.data.LocalAuthRepository
import dev.josecaldera.indicators.login.data.api.AuthApi
import dev.josecaldera.indicators.login.data.api.SecureUserStorage
import dev.josecaldera.indicators.login.data.api.UserStorage
import dev.josecaldera.indicators.login.data.session.LocalSessionStorage
import dev.josecaldera.indicators.login.data.session.SessionStorage
import dev.josecaldera.indicators.login.domain.AuthRepository
import dev.josecaldera.indicators.login.ui.LoginViewModel
import dev.josecaldera.indicators.main.data.IndicatorsRepositoryImpl
import dev.josecaldera.indicators.main.data.api.IndicatorsApi
import dev.josecaldera.indicators.main.data.api.IndicatorsJsonAdapter
import dev.josecaldera.indicators.main.domain.IndicatorsRepository
import dev.josecaldera.indicators.storage.SecureStorageFactory
import dev.josecaldera.indicators.storage.Storage
import dev.josecaldera.indicators.main.ui.IndicatorsViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val BASE_URL = "https://www.mindicador.cl"

val applicationModule = module {

    // ViewModels
    viewModel { LoginViewModel(get(), get()) }
    viewModel { IndicatorsViewModel(get(), get(), get()) }
    viewModel { IndicatorDetailsViewModel(get()) }

    // Storage
    factory<Storage.Factory> { SecureStorageFactory(androidContext()) }
    single<UserStorage> { SecureUserStorage(get()) }
    single<SessionStorage> { LocalSessionStorage(get()) }

    // Network
    single<Moshi> { provideMoshi() }
    single<OkHttpClient> { provideOkHttpClient() }
    single<Retrofit> { provideRetrofit(get(), get()) }

    // API
    single { AuthApi(get()) }
    single<IndicatorsApi> { createIndicatorsApi(get()) }

    // Repositories
    single<AuthRepository> { LocalAuthRepository(get(), get()) }
    single<IndicatorsRepository> { IndicatorsRepositoryImpl(get()) }
}

fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()
}

fun provideMoshi(): Moshi {
    return Moshi.Builder()
        .addLast(IndicatorsJsonAdapter())
        .build()
}

fun provideRetrofit(
    client: OkHttpClient,
    moshi: Moshi
): Retrofit {
    return Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(client)
        .baseUrl(BASE_URL)
        .build()
}

fun createIndicatorsApi(retrofit: Retrofit): IndicatorsApi {
    return retrofit.create(IndicatorsApi::class.java)
}
