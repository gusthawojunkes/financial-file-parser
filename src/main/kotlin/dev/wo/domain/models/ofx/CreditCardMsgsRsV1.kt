package dev.wo.domain.models.ofx

import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "CREDITCARDMSGSRSV1")
class CreditCardMsgsRsV1 {
    @XmlElement(name = "CCSTMTTRNRS")
    private var ccStmtTrnRs: CcStmtTrnRs? = null

    fun getCcStmtTrnRs(): CcStmtTrnRs? {
        return ccStmtTrnRs
    }
}