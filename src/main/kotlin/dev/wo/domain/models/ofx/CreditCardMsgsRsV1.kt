package dev.wo.domain.models.ofx

import dev.wo.domain.models.ofx.CcStmtTrnRs
import javax.xml.bind.annotation.XmlElement

class CreditCardMsgsRsV1 {
    @XmlElement(name = "CCSTMTTRNRS")
    var ccStmtTrnRs: CcStmtTrnRs? = null
}