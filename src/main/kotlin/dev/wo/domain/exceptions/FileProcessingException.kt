package dev.wo.domain.exceptions

import io.ktor.http.HttpStatusCode

class FileProcessingException(override val message: String) : HttpException(HttpStatusCode.BadRequest, message)