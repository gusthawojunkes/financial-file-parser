package dev.wo.application.web.routes

import dev.wo.domain.enums.FinancialInstitution
import dev.wo.infrastructure.factories.TransactionProcessorFactory
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

private suspend fun ApplicationCall.getRequiredHeader(headerName: String): String? {
    return request.headers[headerName] ?: run {
        respond(HttpStatusCode.BadRequest, "$headerName header is required")
        null
    }
}


fun Route.processFileRoutes() {
    route("api/v1") {
        route("file") {
            post("process") {
                val file = call.receiveMultipart()
                var tempFile: File? = null
                val institution = call.getRequiredHeader("Institution") ?: return@post
                val fileType = call.getRequiredHeader("File-Type") ?: return@post

                try {
                    file.forEachPart { part ->
                        if (part is PartData.FileItem) {
                            tempFile = File.createTempFile("upload_", part.originalFileName?.replace(" ", "_") ?: "tempfile")
                            part.streamProvider().use { inputStream ->
                                tempFile!!.outputStream().buffered().use {
                                    inputStream.copyTo(it)
                                }
                            }
                        }
                        part.dispose()
                    }

                    val processor = TransactionProcessorFactory.getProcessor(FinancialInstitution.fromString(institution))
                    processor.withFile(tempFile)
                    processor.withFileType(fileType)
                    processor.processFile()
                } finally {
                    tempFile?.deleteOnExit()
                }
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}


