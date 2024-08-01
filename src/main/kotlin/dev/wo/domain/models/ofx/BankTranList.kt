package dev.wo.domain.models.ofx

import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlElementWrapper

class BankTranList {
    @XmlElement(name = "DTSTART")
    private val dtStart: String? = null

    @XmlElement(name = "DTEND")
    private val dtEnd: String? = null

    @XmlElementWrapper(name = "STMTTRN")
    @XmlElement(name = "STMTTRN")
    var stmtTrnList: List<StmtTrn>? = null
}
