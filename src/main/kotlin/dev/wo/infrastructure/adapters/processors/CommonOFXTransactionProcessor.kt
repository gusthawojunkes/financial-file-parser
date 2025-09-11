package dev.wo.infrastructure.adapters.processors

import dev.wo.domain.common.ProcessingResult
import dev.wo.domain.enums.CardType
import dev.wo.domain.enums.FinancialInstitution
import dev.wo.domain.enums.InvoiceType
import dev.wo.domain.exceptions.FileProcessingException
import dev.wo.domain.models.ofx.accountstatement.OFXFileAccountStatement
import dev.wo.domain.models.ofx.OFXFileCreditInvoice
import dev.wo.domain.models.ofx.StmtTrn
import dev.wo.domain.services.ProcessorConfiguration
import dev.wo.domain.services.TransactionProcessor
import dev.wo.domain.transactions.FinancialTransaction
import dev.wo.infrastructure.adapters.FileService
import dev.wo.infrastructure.helpers.FileDataHelper
import io.ktor.http.*
import org.apache.commons.collections4.CollectionUtils
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import java.io.*

private val logger = LoggerFactory.getLogger(CommonOFXTransactionProcessor::class.java)
private var endingsByProperty: Map<String, String> = mutableMapOf()

open class CommonOFXTransactionProcessor(
    override var file: File? = null,
    override var preferences: ProcessorConfiguration? = null,
    val institution: FinancialInstitution,
) : TransactionProcessor {

    override fun processFile(): ProcessingResult<List<FinancialTransaction>> {
        try {
            this.file ?: throw FileProcessingException("File must be set", institution, "OFX")

            val xmlFile: File? = toXMLFile(file!!)
            val parsedFile: Any? = getParsedFile(xmlFile)

            parsedFile?.let {
                val transactions = when (it) {
                    is OFXFileCreditInvoice -> createFinancialTransactions(it)
                    is OFXFileAccountStatement -> createFinancialTransactions(it)
                    else -> throw FileProcessingException("Unsupported file type", institution, "OFX")
                }
                return ProcessingResult.success(transactions)
            }

            val deleted = xmlFile?.delete()
            logger.debug("Deleted temp file: $deleted")
            return ProcessingResult.success(emptyList())
        } catch (e: FileProcessingException) {
            return ProcessingResult.error(e.message, HttpStatusCode.InternalServerError)
        }

    }

    override fun <T> createFinancialTransactions(data: T): List<FinancialTransaction> {
        val financialTransactions: MutableList<FinancialTransaction> = mutableListOf()
        val transactionsFromFile: List<StmtTrn> = getStatements(data)
        if (CollectionUtils.isEmpty(transactionsFromFile)) return financialTransactions

        val invoiceType = this.preferences?.invoiceType ?: InvoiceType.CREDIT_INVOICE
        val cardType = if (invoiceType == InvoiceType.CREDIT_INVOICE) CardType.CREDIT else CardType.DEBIT

        for (ofxTransaction in transactionsFromFile) {
            val value = ofxTransaction.getTrnAmt()?.toDouble() ?: continue
            val description = ofxTransaction.getMemo() ?: ""
            val transactionTime = FileDataHelper.getDateTime(ofxTransaction.getDtPosted())?: continue
            val institutionUUID = ofxTransaction.getFitId()?: continue
            val transactionType = FileDataHelper.getTransactionType(ofxTransaction.getTrnType())

            val transaction = FinancialTransaction(
                value,
                description,
                transactionTime,
                institutionUUID,
                transactionType,
                institution = this.institution,
                cardType
            )

            financialTransactions.add(transaction)
        }

        return financialTransactions
    }

    fun <T> getStatements(data: T): List<StmtTrn> {
        if (data is OFXFileAccountStatement) {
            data as OFXFileAccountStatement
            return data.getBankMsgsRsV1()?.getStmtTrnRs()?.stmtRs?.bankTranList?.stmtTrnList.orEmpty()
        }

        data as OFXFileCreditInvoice
        return data.getCreditCardMsgsRsV1()?.getCcStmtTrnRs()?.getCcStmtRs()?.getBankTranList()?.getStmtTrnList().orEmpty()
    }

    fun getParsedFile(xmlFile: File?): Any? {
        val parsedFile = when (this.preferences?.invoiceType) {
            InvoiceType.CREDIT_INVOICE -> xmlFile?.let { FileService.unmarshalFile(it, OFXFileCreditInvoice::class.java) }
            InvoiceType.ACCOUNT_STATEMENT -> xmlFile?.let { FileService.unmarshalFile(it, OFXFileAccountStatement::class.java) }
            else -> {
                throw FileProcessingException("Unsupported invoice type: ${this.preferences?.invoiceType}", institution, "OFX")
            }
        }

        return parsedFile
    }

    fun toXMLFile(file: File): File {
        val content: String = turnIntoXMLContent(file)
        val emptyFile: File = File.createTempFile("upload_${System.currentTimeMillis()}", ".xml")
        return FileService.writeNewFile(emptyFile, content)
    }

    private fun turnIntoXMLContent(file: File): String {
        val cleanedContent = StringBuilder()
        val endingsByProperty: Map<String, String> = getEndingsByProperty()
        var lineNumber = 1
        val splitRegex: Regex = ":".toRegex()

        try {
            BufferedReader(FileReader(file)).use { reader ->
                var line: String
                while ((reader.readLine().also { line = it }) != null) {
                    var cleanedLine: String = line.trim { it <= ' ' }
                    if (StringUtils.isBlank(cleanedLine)) continue
                    val needToReformat = cleanedLine.contains(":")
                    if (lineNumber < 10 && needToReformat) {
                        val lineProps: Array<String> = cleanedLine.split(splitRegex).dropLastWhile { it.isEmpty() } .toTypedArray()
                        val property = lineProps[0]
                        val value = lineProps[1]
                        val starting = getStartingFor(property)
                        val ending = getEndingFor(property)

                        cleanedLine = starting + value + ending
                    } else {
                                        if (StringUtils.equals(cleanedLine, "<OFX>")) continue
                        val greaterThanSignIndex: Int = cleanedLine.indexOf('>')
                        val property = cleanedLine.substring(1, greaterThanSignIndex)

                        cleanedLine = closeSTMTTRNIfNeeded(cleanedLine)

                        val starting = getStartingFor(property)
                        val ending = getEndingFor(property)
                        val hasValue = cleanedLine.replace(starting, "").trim { it <= ' ' }.isNotEmpty()

                        if (!cleanedLine.endsWith(ending) && hasValue && endingsByProperty.containsKey(property)) {
                            cleanedLine += endingsByProperty[property]
                        }

                        cleanedLine = startNewTransactionIfNeeded(cleanedLine, property)
                    }
                    startOFXFile(cleanedContent, lineNumber)
                    cleanedContent.append(cleanedLine).append(System.lineSeparator())
                    lineNumber++
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            throw FileProcessingException("Error reading file: ${e.message}", institution, "OFX")
        }

        verifyEmptyContent(cleanedContent, lineNumber)

        return cleanedContent.toString()
    }

    private fun startNewTransactionIfNeeded(cleanedLine: String, property: String): String {
        var line = cleanedLine
        if (StringUtils.equals(property, "DTEND")) {
            line += System.lineSeparator() + "<STMTTRN>"
        }
        return line
    }

    fun startOFXFile(content: StringBuilder, lineNumber: Int) {
        if (lineNumber == 1) content.append("<OFX>").append(System.lineSeparator())
    }

    fun closeSTMTTRNIfNeeded(line: String): String {
        var newLine = line
        if (StringUtils.equals(line, "</BANKTRANLIST>")) {
            newLine = "</STMTTRN>" + System.lineSeparator() + line
        }
        return newLine
    }

    fun verifyEmptyContent(content: StringBuilder, lineNumber: Int) {
        if (StringUtils.isBlank(content) && lineNumber == 1) {
            throw FileProcessingException("File is empty", institution, "OFX")
        }
    }

    private fun getStartingFor(property: String) = "<$property>"

    private fun getEndingFor(property: String) = "</$property>"

    private fun getEndingsByProperty(): Map<String, String> {
        if (endingsByProperty.isEmpty()) {
            val map: MutableMap<String, String> = HashMap()
            map["CODE"] = "</CODE>"
            map["SEVERITY"] = "</SEVERITY>"
            map["LANGUAGE"] = "</LANGUAGE>"
            map["DTSERVER"] = "</DTSERVER>"
            map["CURDEF"] = "</CURDEF>"
            map["ACCTID"] = "</ACCTID>"
            map["DTSTART"] = "</DTSTART>"
            map["DTEND"] = "</DTEND>"
            map["TRNTYPE"] = "</TRNTYPE>"
            map["DTPOSTED"] = "</DTPOSTED>"
            map["TRNAMT"] = "</TRNAMT>"
            map["FITID"] = "</FITID>"
            map["MEMO"] = "</MEMO>"
            map["BALAMT"] = "</BALAMT>"
            map["DTASOF"] = "</DTASOF>"
            map["TRNUID"] = "</TRNUID>"
            endingsByProperty = map
        }

        return endingsByProperty
    }

}
