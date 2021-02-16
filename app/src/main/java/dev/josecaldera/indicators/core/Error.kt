package dev.josecaldera.indicators.core

abstract class Error(
    /** An error message */
    val message: String,
    /** An optional [Throwable] indicating the error cause. */
    val throwable: Throwable? = null
) {
    override fun toString(): String {
        return "Error(message='$message', throwable=$throwable)"
    }
}
