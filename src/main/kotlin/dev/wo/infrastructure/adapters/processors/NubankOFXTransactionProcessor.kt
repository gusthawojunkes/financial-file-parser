package dev.wo.infrastructure.adapters.processors

import dev.wo.domain.common.ProcessingResult
import dev.wo.domain.enums.CardType
import dev.wo.domain.enums.FinancialInstitution
import dev.wo.domain.exceptions.FileProcessingException
import dev.wo.domain.models.ofx.OFXFile
import dev.wo.domain.models.ofx.StmtTrn
import dev.wo.domain.services.ProcessorPreferences
import dev.wo.domain.services.TransactionProcessor
import dev.wo.domain.transactions.FinancialTransaction
import dev.wo.infrastructure.adapters.FileService
import dev.wo.infrastructure.helpers.FileDataHelper
import io.ktor.http.*
import org.apache.commons.collections4.CollectionUtils
import org.apache.commons.lang3.StringUtils
import java.io.*

class NubankOFXTransactionProcessor(
    override var file: File? = null,
    override var preferences: ProcessorPreferences? = null
) : TransactionProcessor {

    @Throws(FileProcessingException::class)
    override fun processFile(): ProcessingResult<List<FinancialTransaction>> {
        try {
            this.file ?: throw FileProcessingException("File must be set")

            val xmlFile: File? = toXMLFile(file!!)
            val parsedFile: OFXFile? = xmlFile?.let { FileService.unmarshalFile(it, OFXFile::class.java) }

            parsedFile?.let {
                return ProcessingResult.success(createFinancialTransactions(it))
            }

            xmlFile?.delete()

            return ProcessingResult.success(emptyList())
        } catch (e: FileProcessingException) {
            return ProcessingResult.error(e.message, HttpStatusCode.InternalServerError)
        }

    }

    override fun <T> createFinancialTransactions(data: T): List<FinancialTransaction> {
        val financialTransactions: MutableList<FinancialTransaction> = mutableListOf()
        data as OFXFile
        val transactionsFromFile: List<StmtTrn> = data.getCreditCardMsgsRsV1()?.getCcStmtTrnRs()?.getCcStmtRs()?.getBankTranList()?.getStmtTrnList().orEmpty()
        if (CollectionUtils.isEmpty(transactionsFromFile)) return financialTransactions

        for (ofxTransaction in transactionsFromFile) {
            val value = ofxTransaction.getTrnAmt()?.toDouble() ?: continue
            val description = ofxTransaction.getMemo() ?: ""
            val transactionTime = FileDataHelper.getDate(ofxTransaction.getDtPosted())?: continue
            val institutionUUID = ofxTransaction.getFitId()?: continue
            val transactionType = FileDataHelper.getTransactionType(ofxTransaction.getTrnType())
            val cardType = CardType.CREDIT // at this moment I have only credit card invoices
            val transaction = FinancialTransaction(
                value,
                description,
                transactionTime,
                institutionUUID,
                transactionType,
                FinancialInstitution.NUBANK,
                cardType
            )

            financialTransactions.add(transaction)
        }

        return financialTransactions
    }

    fun toXMLFile(file: File): File {
        val content: String = turnIntoXMLContent(file)
        val emptyFile: File = File.createTempFile("upload_${System.currentTimeMillis()}", ".xml")
        return FileService.writeNewFile(emptyFile, content)
    }

    private fun turnIntoXMLContent(file: File): String {
        val cleanedContent = StringBuilder()
        val endingsByProperty = getEndingsByProperty()
        var lineNumber = 1
        val splitRegex = ":".toRegex()

        try {
            BufferedReader(FileReader(file)).use { reader ->
                var line: String
                while ((reader.readLine().also { line = it }) != null) {
                    var cleanedLine: String? = line.trim { it <= ' ' }
                    if (StringUtils.isNotBlank(cleanedLine)) {
                        val needToReformat = cleanedLine!!.contains(":")
                        if (lineNumber < 10 && needToReformat) {
                            val lineProps: Array<String> = cleanedLine.split(splitRegex).dropLastWhile { it.isEmpty() } .toTypedArray()
                            val property = lineProps[0]
                            val value = lineProps[1]
                            val starting = getStartingFor(property)
                            val ending = getEndingFor(property)

                            cleanedLine = starting + value + ending
                        } else {
                            if (StringUtils.equals(cleanedLine, "<OFX>")) continue
                            val greaterThanSignIndex = cleanedLine!!.indexOf('>')
                            val property = cleanedLine!!.substring(1, greaterThanSignIndex)

                            if (StringUtils.equals(cleanedLine, "</BANKTRANLIST>")) {
                                cleanedLine = "</STMTTRN>" + System.lineSeparator() + cleanedLine
                            }

                            val starting = getStartingFor(property)
                            val ending = getEndingFor(property)
                            val hasValue = cleanedLine.replace(starting, "").trim { it <= ' ' }.isNotEmpty()

                            if (!cleanedLine.endsWith(ending) && hasValue && endingsByProperty.containsKey(property)) {
                                cleanedLine += endingsByProperty[property]
                            }

                            if (StringUtils.equals(property, "DTEND")) {
                                cleanedLine += System.lineSeparator() + "<STMTTRN>"
                            }
                        }
                    }
                    if (lineNumber == 1) cleanedContent.append("<OFX>").append(System.lineSeparator())
                    cleanedContent.append(cleanedLine).append(System.lineSeparator())
                    lineNumber++
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            throw FileProcessingException("Error reading file: ${e.message}")
        }

        if (StringUtils.isBlank(cleanedContent.toString()) && lineNumber == 1) {
            throw FileProcessingException("File is empty")
        }

        return cleanedContent.toString()
    }

    private fun getStartingFor(property: String) = "<$property>"

    private fun getEndingFor(property: String) = "</$property>"

    private fun getEndingsByProperty(): Map<String, String> {
        val endingsByProperty: MutableMap<String, String> = HashMap()
        endingsByProperty["CODE"] = "</CODE>"
        endingsByProperty["SEVERITY"] = "</SEVERITY>"
        endingsByProperty["LANGUAGE"] = "</LANGUAGE>"
        endingsByProperty["DTSERVER"] = "</DTSERVER>"
        endingsByProperty["CURDEF"] = "</CURDEF>"
        endingsByProperty["ACCTID"] = "</ACCTID>"
        endingsByProperty["DTSTART"] = "</DTSTART>"
        endingsByProperty["DTEND"] = "</DTEND>"
        endingsByProperty["TRNTYPE"] = "</TRNTYPE>"
        endingsByProperty["DTPOSTED"] = "</DTPOSTED>"
        endingsByProperty["TRNAMT"] = "</TRNAMT>"
        endingsByProperty["FITID"] = "</FITID>"
        endingsByProperty["MEMO"] = "</MEMO>"
        endingsByProperty["BALAMT"] = "</BALAMT>"
        endingsByProperty["DTASOF"] = "</DTASOF>"
        endingsByProperty["TRNUID"] = "</TRNUID>"
        return endingsByProperty
    }

}
