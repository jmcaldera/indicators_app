package dev.josecaldera.indicators.login.data.api

import dev.josecaldera.indicators.storage.Storage

interface UserStorage {

    fun storeUsers(users: List<UserEntity>)

    fun getUser(email: String): UserEntity?
}

/**
 * Simulates a secure remote data base that stores encrypted user data
 */
class SecureUserStorage(
    storageFactory: Storage.Factory
) : UserStorage {

    companion object {
        private const val FILE_NAME = "user_storage"
    }

    private val storage = storageFactory.create(FILE_NAME)

    override fun storeUsers(users: List<UserEntity>) {
        users.forEach {
            storage.putString(it.email, it.password)
        }
    }

    override fun getUser(email: String): UserEntity? {
        return storage.getString(email)?.let { password ->
            UserEntity(
                email = email,
                password = password
            )
        }
    }
}
