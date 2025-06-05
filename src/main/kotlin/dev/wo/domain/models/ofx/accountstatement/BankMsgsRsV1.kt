package dev.wo.domain.models.ofx.accountstatement

import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "BANKMSGSRSV1")
class BankMsgsRsV1 {
    @XmlElement(name = "STMTTRNRS")
    private var stmtTrnRs: StmtTrnRs? = null

    fun getStmtTrnRs(): StmtTrnRs? {
        return stmtTrnRs
    }

}