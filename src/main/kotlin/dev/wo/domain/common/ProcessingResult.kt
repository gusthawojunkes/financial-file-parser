package dev.wo.domain.common

import io.ktor.http.*

class ProcessingResult<T> (
    val message: String,
    val status: HttpStatusCode,
    val data: T? = null
) {
    companion object {
        fun <T> success(data: T): ProcessingResult<T> = ProcessingResult(
            data = data,
            message = "Success",
            status = HttpStatusCode.OK
        )
        fun <T> error(message: String, status: HttpStatusCode): ProcessingResult<T> = ProcessingResult(
            message = message,
            status = status
        )
    }
}