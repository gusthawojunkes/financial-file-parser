package dev.wo.domain.models.ofx

import javax.xml.bind.annotation.XmlElement

class CcStmtTrnRs {
    @XmlElement(name = "TRNUID")
    private val trnUid: String? = null

    @XmlElement(name = "STATUS")
    private val status: Status? = null

    @XmlElement(name = "CCSTMTRS")
    var ccStmtRs: CcStmtRs? = null
}
