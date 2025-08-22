package dev.wo.infrastructure.factories

import dev.wo.domain.enums.FinancialInstitution
import dev.wo.domain.enums.FinancialInstitution.*
import dev.wo.domain.exceptions.FileProcessingException
import dev.wo.domain.services.TransactionProcessor
import dev.wo.infrastructure.adapters.processors.BradescoCSVTransactionProcessor
import dev.wo.infrastructure.adapters.processors.C6CSVTransactionProcessor
import dev.wo.infrastructure.adapters.processors.C6OFXTransactionProcessor
import dev.wo.infrastructure.adapters.processors.CommonCSVTransactionProcessor
import dev.wo.infrastructure.adapters.processors.CommonOFXTransactionProcessor
import dev.wo.infrastructure.adapters.processors.NubankOFXTransactionProcessor
import dev.wo.infrastructure.adapters.processors.WiseCSVTransactionProcessor

object TransactionProcessorFactory {
    fun getProcessor(institution: FinancialInstitution, fileType: String): TransactionProcessor {
        val processorFileType = fileType.lowercase()

        return when (institution) {
            NUBANK -> when (processorFileType) {
                "ofx" -> NubankOFXTransactionProcessor()
                else -> throw FileProcessingException("Invalid file type for Nubank",
                    NUBANK, fileType)
            }
            WISE -> when (processorFileType) {
                "csv" -> WiseCSVTransactionProcessor()
                else -> throw FileProcessingException("Invalid file type for Wise", WISE, fileType)
            }
            C6_BANK -> when(processorFileType) {
                "ofx" -> C6OFXTransactionProcessor()
                "csv" -> C6CSVTransactionProcessor()
                else -> throw FileProcessingException("Invalid file type for C6 Bank", C6_BANK, fileType)
            }
            BRADESCO -> when(processorFileType) {
                "csv" -> BradescoCSVTransactionProcessor()
                else -> throw FileProcessingException("Invalid file type for Bradesco", BRADESCO, fileType)
            }
            ANY -> when (processorFileType) {
                "csv" -> CommonCSVTransactionProcessor()
                "ofx" -> CommonOFXTransactionProcessor(institution = ANY)
                else -> throw FileProcessingException("Invalid file type for Any", ANY, fileType)
            }
            else -> throw FileProcessingException("Invalid financial institution", UNKNOWN, fileType)
        }
    }
}