package dev.josecaldera.indicators.storage

/**
 * Basic representation of a data storage that allows to store string data
 */
interface Storage {

    // Expand to allow more types
    fun putString(key: String, value: String)
    fun getString(key: String): String?
    fun remove(key: String)
    fun clear()

    interface Factory {
        fun create(fileName: String): Storage
    }
}
