package dev.josecaldera.indicators.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.josecaldera.indicators.login.data.api.SecureUserStorage
import dev.josecaldera.indicators.login.data.api.UserStorage
import dev.josecaldera.indicators.login.data.session.LocalSessionStorage
import dev.josecaldera.indicators.login.data.session.SessionStorage
import dev.josecaldera.indicators.storage.SecureStorageFactory
import dev.josecaldera.indicators.storage.Storage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    fun provideStorageFactory(@ApplicationContext context: Context): Storage.Factory {
        return SecureStorageFactory(context)
    }

    @Singleton
    @Provides
    fun provideUserStorage(factory: Storage.Factory): UserStorage {
        return SecureUserStorage(factory)
    }

    @Singleton
    @Provides
    fun provideSessionStorage(factory: Storage.Factory): SessionStorage {
        return LocalSessionStorage(factory)
    }
}
