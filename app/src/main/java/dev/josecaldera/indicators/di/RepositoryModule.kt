package dev.josecaldera.indicators.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.josecaldera.indicators.login.data.LocalAuthRepository
import dev.josecaldera.indicators.login.data.api.AuthApi
import dev.josecaldera.indicators.login.data.session.SessionStorage
import dev.josecaldera.indicators.login.domain.AuthRepository
import dev.josecaldera.indicators.main.data.IndicatorsRepositoryImpl
import dev.josecaldera.indicators.main.data.api.IndicatorsApi
import dev.josecaldera.indicators.main.domain.IndicatorsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideAuthRepository(
        authApi: AuthApi,
        sessionStorage: SessionStorage
    ): AuthRepository {
        return LocalAuthRepository(authApi, sessionStorage)
    }

    @Singleton
    @Provides
    fun provideIndicatorsRepository(
        indicatorsApi: IndicatorsApi
    ): IndicatorsRepository {
        return IndicatorsRepositoryImpl(indicatorsApi)
    }
}
