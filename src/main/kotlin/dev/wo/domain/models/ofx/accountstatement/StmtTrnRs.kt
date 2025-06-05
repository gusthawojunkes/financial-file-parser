package dev.wo.domain.models.ofx.accountstatement

import dev.wo.domain.models.ofx.Status
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "STMTTRNRS")
class StmtTrnRs {

    @get:XmlElement(name = "TRNUID")
    private var trnUid: String? = null

    @get:XmlElement(name = "STATUS")
    var status: Status? = null

    @get:XmlElement(name = "STMTRS")
    var stmtRs: StmtRs? = null
}