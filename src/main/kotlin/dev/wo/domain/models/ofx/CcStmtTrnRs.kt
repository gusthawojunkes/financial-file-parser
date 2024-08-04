package dev.wo.domain.models.ofx

import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "CCSTMTTRNRS")
class CcStmtTrnRs {
    @XmlElement(name = "TRNUID")
    private val trnUid: String? = null

    @XmlElement(name = "STATUS")
    private val status: Status? = null

    @XmlElement(name = "CCSTMTRS")
    private var ccStmtRs: CcStmtRs? = null

    fun getTrnUid(): String? {
        return trnUid
    }

    fun getStatus(): Status? {
        return status
    }

    fun getCcStmtRs(): CcStmtRs? {
        return ccStmtRs
    }
}
