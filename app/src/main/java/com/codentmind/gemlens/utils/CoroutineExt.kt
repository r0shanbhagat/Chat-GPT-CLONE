package com.codentmind.gemlens.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.Continuation

/**
 * @Details :CoroutineExt
 * @Author Roshan Bhagat
 */

/**
 * postDelay: This function is an extension function for CoroutineScope that allows you to
 *
 * @param delayInMillis
 * @param block
 * @receiver
 */
inline fun CoroutineScope.postDelay(
    delayInMillis: Long,
    crossinline block: () -> Unit
) = launch {
    delay(delayInMillis)
    block()
}

/**
 * Suspend coroutine with timeout
 *
 * @param T
 * @param timeout
 * @param block
 * @receiver
 */
suspend inline fun <T> suspendCoroutineWithTimeout(
    timeout: Long = 10000L,
    crossinline block: (Continuation<T>) -> Unit
) = withTimeoutOrNull(timeout) {
    // This is a suspend function, so we can use suspendCancellableCoroutine
    suspendCancellableCoroutine(block)
}

/**
 * Example:
suspend fun getID() = suspendCoroutineWithTimeout<String> { continuation ->
// Simulate a long-running operation
Thread.sleep(2000)
continuation.resume("ID")
}
 **/