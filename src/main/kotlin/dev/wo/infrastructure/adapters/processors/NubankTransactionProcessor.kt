package dev.wo.infrastructure.adapters.processors

import com.google.gson.Gson
import dev.wo.domain.enums.FinancialInstitution
import dev.wo.domain.enums.TransactionType
import dev.wo.domain.models.ofx.FinancialTransaction
import dev.wo.domain.models.ofx.OFXDataHelper
import dev.wo.domain.models.ofx.OFXFile
import dev.wo.domain.models.ofx.StmtTrn
import dev.wo.domain.services.TransactionProcessor
import java.io.File
import javax.xml.bind.JAXBContext

class NubankTransactionProcessor(
    override val institution: FinancialInstitution,
    override var fileType: String? = null,
    override var file: File? = null
) : TransactionProcessor {

    override fun processFile() {
        this.fileType ?: throw IllegalArgumentException("File type must be set")
        this.file ?: throw IllegalArgumentException("File must be set")
        when (this.fileType?.lowercase()) {
            "ofx" -> processOfxFile(this.file!!)
            else -> throw IllegalArgumentException("Invalid file type")
        }
    }

    override fun createFinancialTransactions(data: OFXFile): MutableList<FinancialTransaction> {
        val financialTransactions: MutableList<FinancialTransaction> = mutableListOf()
        val transactionsFromFile: List<StmtTrn>? = data.creditCardMsgsRsV1?.ccStmtTrnRs?.ccStmtRs?.bankTranList?.stmtTrnList
        if (transactionsFromFile != null) {
            for (ofxTransaciton in transactionsFromFile) {
                val value = ofxTransaciton.trnAmt!!.toDouble()
                val description = ofxTransaciton.memo!!
                val transactionTime = OFXDataHelper.getDate(ofxTransaciton.dtPosted!!)!!
                val transaction = FinancialTransaction(
                    value,
                    description,
                    transactionTime,
                    transactionType = TransactionType.CREDIT,
                    institution = this.institution
                )

                financialTransactions.add(transaction)
            }
        }

        return financialTransactions
    }

    private fun processOfxFile(file: File): OFXFile? {
        val data: OFXFile? = with (file) {
            var data: OFXFile? = null
            try {
                val context: JAXBContext = JAXBContext.newInstance(OFXFile::class.java)
                val unmarshaller = context.createUnmarshaller()
                data = unmarshaller.unmarshal(this) as OFXFile?
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return data
        }

        val transactions = this.createFinancialTransactions(data!!)
        val gson = Gson()
        println("Transactions: ${gson.toJson(transactions)}")
        println("Processed file")
    }

}
