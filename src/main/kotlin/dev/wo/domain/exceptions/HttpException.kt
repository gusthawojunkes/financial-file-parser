package dev.wo.domain.exceptions

import io.ktor.http.*

class HttpException(
    val status: HttpStatusCode,
    override val message: String
) : Exception(message)