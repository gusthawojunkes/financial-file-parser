package dev.wo.domain.exceptions

import io.ktor.http.*

open class HttpException(
    val status: HttpStatusCode,
    override val message: String
) : Exception(message)