package dev.wo.domain.models.ofx

import javax.xml.bind.annotation.XmlElement

class CcStmtRs {
    @XmlElement(name = "CURDEF")
    private val curDef: String? = null

    @XmlElement(name = "CCACCTFROM")
    private val ccAcctFrom: CcAcctFrom? = null

    @XmlElement(name = "BANKTRANLIST")
    var bankTranList: BankTranList? = null

    @XmlElement(name = "LEDGERBAL")
    private val ledgerBal: LedgerBal? = null
}