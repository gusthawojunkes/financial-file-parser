package dev.wo.domain.models.ofx.accountstatement

import dev.wo.domain.models.ofx.LedgerBal
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "STMTRS")
class StmtRs {

    @get:XmlElement(name = "CURDEF")
    var curDef: String? = null

    @get:XmlElement(name = "BANKACCTFROM")
    var bankAcctFrom: BankAcctFrom? = null

    @get:XmlElement(name = "BANKTRANLIST")
    var bankTranList: BankTranList? = null

    @get:XmlElement(name = "LEDGERBAL")
    var ledgerBal: LedgerBal? = null

    @get:XmlElement(name = "BALLIST")
    var balList: BalList? = null

}