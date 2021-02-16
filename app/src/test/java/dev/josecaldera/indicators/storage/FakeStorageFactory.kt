package dev.josecaldera.indicators.storage

import io.mockk.spyk

class FakeStorageFactory : Storage.Factory {

    val storage = spyk(FakeStorage())

    override fun create(fileName: String): Storage {
        return storage
    }
}

class FakeStorage : Storage {

    val data: MutableMap<String, String> = mutableMapOf()

    override fun putString(key: String, value: String) {
        data[key] = value
    }

    override fun getString(key: String): String? {
        return data[key]
    }

    override fun remove(key: String) {
        data.remove(key)
    }

    override fun clear() {
        data.clear()
    }
}