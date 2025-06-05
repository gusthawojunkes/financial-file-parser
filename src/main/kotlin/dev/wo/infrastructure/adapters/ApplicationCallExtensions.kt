package dev.wo.infrastructure.adapters

import dev.wo.domain.enums.InvoiceType
import dev.wo.domain.exceptions.HttpException
import dev.wo.domain.services.ProcessorConfiguration
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

suspend fun ApplicationCall.getTempFile(): File {
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

fun ApplicationCall.getRequiredHeader(headerName: String): String {
    return request.headers[headerName] ?: run {
        throw HttpException(HttpStatusCode.BadRequest, "$headerName header is required")
    }
}

fun ApplicationCall.getPreferences(): ProcessorConfiguration {
    val separator: Char = request.headers["CSV-Separator"]?.firstOrNull() ?: ','
    val dateTimePattern: String = request.headers["DateTime-Pattern"]?: "dd/MM/yyyy HH:mm:ss"
    val invoiceType = InvoiceType.fromString(this.getRequiredHeader("Invoice-Type"))

    return ProcessorConfiguration(separator, dateTimePattern, invoiceType)
}
