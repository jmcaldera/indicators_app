package dev.josecaldera.indicators.core

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

sealed class Result<out T> {

    data class OnSuccess<T>(val data: T) : Result<T>()
    data class OnError<T>(val error: Error) : Result<T>()

    override fun toString(): String {
        return when (this) {
            is OnSuccess<*> -> "OnSuccess[data=$data]"
            is OnError -> "OnError[error=$error]"
        }
    }

    /**
     * Returns `true` if this instance represents successful outcome.
     * In this case [isError] returns `false`.
     */
    fun isSuccess(): Boolean = this is OnSuccess

    /**
     * Returns `true` if this instance represents failed outcome.
     * In this case [isSuccess] returns `false`.
     */
    fun isError(): Boolean = this is OnError

    /**
     * Returns the encapsulated value if this instance represents [success][Result.isSuccess] or `null`
     * if it is [failure][Result.isError].
     *
     * This function is shorthand for `getOrElse { null }` (see [getOrElse]) or
     * `fold(onSuccess = { it }, onFailure = { null })` (see [fold]).
     */
    fun getOrNull(): T? = when (this) {
        is OnSuccess -> data
        is OnError -> null
    }

    /**
     * Returns the encapsulated error if this instance represents [failure][Result.isError] or `null`
     * if it is [success][Result.isSuccess].
     *
     * This function is shorthand for `getOrElse { null }` (see [getOrElse]) or
     * `fold(onSuccess = { it }, onFailure = { null })` (see [fold]).
     */
    inline fun <reified T : Error> errorOrNull(): T? {
        return when (this) {
            is OnSuccess -> null
            is OnError -> if (error is T) error else null
        }
    }

    /**
     * Returns the encapsulated exception if this instance represents [failure][isError] or `null`
     * if it is [success][isSuccess].
     *
     * This function is shorthand for `fold(onSuccess = { null }, onFailure = { it })` (see [fold]).
     */
    fun exceptionOrNull(): Throwable? = when (this) {
        is OnError -> error.throwable
        else -> null
    }
}

/**
 * Returns the encapsulated result of the given [transform] function applied to encapsulated value
 * if this instance represents [success][Result.OnSuccess] or the
 * original encapsulated exception if it is [Result.OnError].
 *
 * Note, that an exception thrown by [transform] function is rethrown by this function.
 */
fun <R, T> Result<T>.map(transform: (value: T) -> R): Result<R> {
    return when (this) {
        is Result.OnSuccess -> Result.OnSuccess(transform(data))
        is Result.OnError -> Result.OnError(error)
    }
}

/**
 * Performs the given [action] on encapsulated value if this instance represents [success][Result.isSuccess].
 * Returns the original `Result` unchanged.
 */
@ExperimentalContracts
inline fun <T> Result<T>.onSuccess(action: (value: T) -> Unit): Result<T> {
    contract {
        callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }
    if (isSuccess()) action((this as Result.OnSuccess).data)
    return this
}

