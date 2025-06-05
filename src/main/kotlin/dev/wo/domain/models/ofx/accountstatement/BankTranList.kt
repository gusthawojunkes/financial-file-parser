package dev.wo.domain.models.ofx.accountstatement

import dev.wo.domain.models.ofx.StmtTrn
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlElementWrapper
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "BANKTRANLIST")
class BankTranList {

    @get:XmlElement(name = "DTSTART")
    var dtStart: String? = null

    @get:XmlElement(name = "DTEND")
    var dtEnd: String? = null

    @get:XmlElementWrapper(name = "STMTTRN")
    @get:XmlElement(name = "STMTTRN")
    var stmtTrnList: List<StmtTrn>? = null

}