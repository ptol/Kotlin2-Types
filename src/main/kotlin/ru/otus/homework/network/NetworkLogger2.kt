@file:Suppress("unused")
package ru.otus.homework.network
import ru.otus.homework.Shape
import java.lang.IllegalArgumentException
import java.time.LocalDateTime

/**
 * Известный вам список ошибок
 */
sealed class ApiException2(message: String) : Throwable(message) {
    data object NotAuthorized : ApiException2("Not authorized")
    data object NetworkException : ApiException2("Not connected")
    data object UnknownException: ApiException2("Unknown exception")
}

class ErrorLogger2<T : Throwable> {
    private val errors = mutableListOf<Pair<LocalDateTime, T>>()

    fun <E : T> log(response: NetworkResponse<*, E>) {
        if (response is Failure) {
            errors.add(response.responseDateTime to response.error)
        }
    }

    fun dump(): List<Pair<LocalDateTime, T>> = errors.toList()

    fun dumpLog() {
        errors.forEach { (date, error) ->
            println("Error at $date: ${error.message}")
        }
    }
}

fun processThrowables(logger: ErrorLogger2<Throwable>) {
    logger.log(Success("Success"))
    Thread.sleep(100)
    logger.log(Success(Shape()))
    Thread.sleep(100)
    logger.log(Failure(IllegalArgumentException("Something unexpected")))
    logger.dumpLog()
}

fun processApiErrors(apiExceptionLogger: ErrorLogger2<in ApiException2>) {
    apiExceptionLogger.log(Success("Success"))
    Thread.sleep(100)
    apiExceptionLogger.log(Success(Shape()))
    Thread.sleep(100)
    apiExceptionLogger.log(Failure(ApiException2.NetworkException))
    apiExceptionLogger.dumpLog()
}

fun main() {
    val logger = ErrorLogger2<Throwable>()
    println("Processing Throwable:")
    processThrowables(logger)
    println("Processing Api:")
    processApiErrors(logger)
}

