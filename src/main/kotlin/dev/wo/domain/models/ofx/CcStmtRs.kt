package dev.wo.domain.models.ofx

import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "CCSTMTRS")
class CcStmtRs {
    @XmlElement(name = "CURDEF")
    private val curDef: String? = null

    @XmlElement(name = "CCACCTFROM")
    private val ccAcctFrom: CcAcctFrom? = null

    @XmlElement(name = "BANKTRANLIST")
    private var bankTranList: BankTranList? = null

    @XmlElement(name = "LEDGERBAL")
    private val ledgerBal: LedgerBal? = null

    fun getCurDef(): String? {
        return curDef
    }

    fun getCcAcctFrom(): CcAcctFrom? {
        return ccAcctFrom
    }

    fun getBankTranList(): BankTranList? {
        return bankTranList
    }

    fun getLedgerBal(): LedgerBal? {
        return ledgerBal
    }
}