package dev.wo.infrastructure.adapters

import dev.wo.domain.exceptions.HttpException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

public suspend fun ApplicationCall.getTempFile(): File {
    return withContext(Dispatchers.IO) {
        val tempFile = File.createTempFile("upload_${System.currentTimeMillis()}", ".tmp")
        receiveStream().use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        tempFile
    }
}

public suspend fun ApplicationCall.getRequiredHeader(headerName: String): String {
    return request.headers[headerName] ?: run {
        throw HttpException(HttpStatusCode.BadRequest, "$headerName header is required")
    }
}
