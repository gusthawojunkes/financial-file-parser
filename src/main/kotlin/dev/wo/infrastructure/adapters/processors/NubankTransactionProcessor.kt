package dev.wo.infrastructure.adapters.processors

import dev.wo.domain.enums.CardType
import dev.wo.domain.enums.FinancialInstitution
import dev.wo.domain.transactions.FinancialTransaction
import dev.wo.domain.models.ofx.OFXDataHelper
import dev.wo.domain.models.ofx.OFXFile
import dev.wo.domain.models.ofx.StmtTrn
import dev.wo.domain.services.TransactionProcessor
import org.apache.commons.lang3.StringUtils
import java.io.*
import javax.xml.bind.JAXBContext

private val INSTITUTION: FinancialInstitution = FinancialInstitution.NUBANK

class NubankTransactionProcessor(
    override var fileType: String? = null,
    override var file: File? = null
) : TransactionProcessor {

    override fun processFile(): MutableList<FinancialTransaction> {
        this.fileType ?: throw IllegalArgumentException("File type must be set")
        this.file ?: throw IllegalArgumentException("File must be set")

        return when (this.fileType?.lowercase()) {
            "ofx" -> processOfxFile(this.file!!)
            else -> throw IllegalArgumentException("Invalid file type")
        }
    }

    private fun processOfxFile(file: File): MutableList<FinancialTransaction> {
        val organizedFile = organizeFile(file)
        val newFile = organizedFile?.let { unmarshalFile(it) }

        newFile?.let {
            val transactions = createFinancialTransactions(it)
            return transactions
        }

        return mutableListOf()
    }

    override fun createFinancialTransactions(data: OFXFile): MutableList<FinancialTransaction> {
        val financialTransactions: MutableList<FinancialTransaction> = mutableListOf()
        val transactionsFromFile: List<StmtTrn>? = data.getCreditCardMsgsRsV1()?.getCcStmtTrnRs()?.getCcStmtRs()?.getBankTranList()?.getStmtTrnList()
        if (transactionsFromFile != null) {
            for (ofxTransaction in transactionsFromFile) {
                val value = ofxTransaction.getTrnAmt()!!.toDouble()
                val description = ofxTransaction.getMemo()!!
                val transactionTime = OFXDataHelper.getDate(ofxTransaction.getDtPosted())!!
                val institutionUUID = ofxTransaction.getFitId()!!
                val transactionType = OFXDataHelper.getTransactionType(ofxTransaction.getTrnType())
                val cardType = CardType.CREDIT // at this moment I have only credit card invoices
                val transaction = FinancialTransaction(
                    value,
                    description,
                    transactionTime,
                    institutionUUID,
                    transactionType = transactionType,
                    institution = INSTITUTION,
                    cardType = cardType
                )

                financialTransactions.add(transaction)
            }
        }

        return financialTransactions
    }

    private fun organizeFile(file: File?): File? {
        try {
            return this.transformIntoXML(file!!)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return file
        }
    }

    private fun unmarshalFile(file: File): OFXFile? {
        return try {
            val context: JAXBContext = JAXBContext.newInstance(OFXFile::class.java)
            val unmarshaller = context.createUnmarshaller()
            unmarshaller.unmarshal(file) as OFXFile?
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun transformIntoXML(file: File): File {
        val content = readAndTransform(file)
        val temp = writeNewFile(file, content)
        return temp
    }

    private fun readAndTransform(file: File): String {
        val cleanedContent = StringBuilder()
        val endingsByProperty = getEndingsByProperty()
        try {
            BufferedReader(FileReader(file)).use { reader ->
                var line: String
                var lineNumber = 1
                while ((reader.readLine().also { line = it }) != null) {
                    var cleanedLine: String? = line.trim { it <= ' ' }
                    if (StringUtils.isNotBlank(cleanedLine)) {
                        val needToReformat = cleanedLine!!.contains(":")
                        if (lineNumber < 10 && needToReformat) {
                            val lineProps =
                                cleanedLine.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                            val property = lineProps[0]
                            val value = lineProps[1]
                            cleanedLine = getStartingFor(property) + value + getEndingFor(property)
                        } else {
                            if (StringUtils.equals(cleanedLine, "<OFX>")) continue
                            val greaterThanSignIndex = cleanedLine!!.indexOf('>')
                            val property = cleanedLine!!.substring(1, greaterThanSignIndex)

                            if (StringUtils.equals(cleanedLine, "</BANKTRANLIST>")) {
                                cleanedLine = "</STMTTRN>" + System.lineSeparator() + cleanedLine
                            }

                            val hasValue =
                                cleanedLine.replace(getStartingFor(property), "").trim { it <= ' ' }.isNotEmpty()
                            if (!cleanedLine.endsWith(getEndingFor(property)) && hasValue && endingsByProperty.containsKey(
                                    property
                                )
                            ) {
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
            System.err.println("Erro ao ler o arquivo de entrada: " + e.message)
        }

        return cleanedContent.toString()
    }

    private fun writeNewFile(file: File, cleanedContent: String): File {
        try {
            BufferedWriter(FileWriter(file)).use { writer ->
                writer.write(cleanedContent)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return file
    }

    fun getStartingFor(property: String): String {
        return "<$property>"
    }

    fun getEndingFor(property: String): String {
        return "</$property>"
    }

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
