package dev.wo.infrastructure.adapters.processors

import dev.wo.domain.exceptions.FileProcessingException
import dev.wo.domain.models.ofx.OFXFile
import dev.wo.domain.models.ofx.StmtTrn
import dev.wo.infrastructure.adapters.FileService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertThrows
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NubankOFXTransactionProcessorTest {

    @Test
    fun `when processFile is called without a file then it should throw an FileProcessingException`() {
        val processor = NubankOFXTransactionProcessor(file = null)

        val exception = assertThrows(FileProcessingException::class.java) {
            processor.processFile()
        }

        assertEquals("File must be set", exception.message)
    }

    @Test
    fun `when processFile is called with a non empty file then it should return a list of transactions`() {
        val processor = NubankOFXTransactionProcessor(
            file = File("src/test/resources/files/ofx/nubank.ofx")
        )

        val transactions = processor.processFile()

        assertTrue(transactions.isNotEmpty())
    }

    @Test
    fun `when processFile is called with a empty file then it should return a empty list`() {
        val processor = NubankOFXTransactionProcessor(
            file = File("src/test/resources/files/ofx/empty.ofx")
        )

        val transactions = processor.processFile()

        assertTrue(transactions.isEmpty())
    }

    @Test
    fun `when createFinancialTransactions is called with a OFXFile then it should return a list of FinancialTransaction`() {
        val processor = NubankOFXTransactionProcessor()

        val ofxFile = mockk<OFXFile>()
        val stmtTrn = mockk<StmtTrn>()

        every { stmtTrn.getTrnAmt() } returns "10.0"
        every { stmtTrn.getMemo() } returns "description"
        every { stmtTrn.getDtPosted() } returns "20240628000000[-3:GMT]"
        every { stmtTrn.getFitId() } returns "uuid"
        every { stmtTrn.getTrnType() } returns "CREDIT"
        every { ofxFile.getCreditCardMsgsRsV1()?.getCcStmtTrnRs()?.getCcStmtRs()?.getBankTranList()?.getStmtTrnList() } returns listOf(stmtTrn)

        val transactions = processor.createFinancialTransactions(ofxFile)

        assertTrue(transactions.isNotEmpty())
    }

}