package dev.wo.application.services

import dev.wo.application.web.resource.response.FinancialTransactionResponse
import dev.wo.domain.enums.FinancialInstitution
import dev.wo.domain.exceptions.HttpException
import dev.wo.domain.services.ProcessorConfiguration
import dev.wo.infrastructure.adapters.FileService
import dev.wo.infrastructure.factories.TransactionProcessorFactory
import io.ktor.http.HttpStatusCode
import java.io.File

class ProcessFileService {

    fun process(
        institution: FinancialInstitution,
        fileType: String,
        file: File,
        preferences: ProcessorConfiguration
    ): List<FinancialTransactionResponse> {
        FileService.validateFileType(fileType)

        val processor = TransactionProcessorFactory.getProcessor(institution, fileType) {
            this.file = file
            this.preferences = preferences
        }

        val result = processor.processFile()

        if (result.data == null || result.data.isEmpty()) {
            throw HttpException(HttpStatusCode.NoContent, "No transactions found")
        }

        return result.data.map { FinancialTransactionResponse.from(it) }
    }
}