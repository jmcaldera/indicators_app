package dev.josecaldera.indicators.storage

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * [Storage.Factory] that creates an secure storage [EncryptedStorage]
 * @param context Application Context
 */
class SecureStorageFactory(
    private val context: Context
) : Storage.Factory {

    override fun create(fileName: String): Storage {
        return EncryptedStorage(context, fileName)
    }
}

/**
 * Secure storage that uses an [EncryptedSharedPreferences] internally to
 * save encrypted data and decrypts it when reading it.
 */
class EncryptedStorage(
    context: Context,
    fileName: String
) : Storage {

    private val preferences = EncryptedSharedPreferences.create(
        context,
        fileName,
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override fun putString(key: String, value: String) {
        preferences.edit { putString(key, value) }
    }

    override fun getString(key: String): String? {
        return preferences.getString(key, null)
    }

    override fun remove(key: String) {
        preferences.edit { remove(key) }
    }

    override fun clear() {
        preferences.edit { clear() }
    }
}
