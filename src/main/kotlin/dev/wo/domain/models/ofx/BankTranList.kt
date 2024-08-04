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
    private var stmtTrnList: List<StmtTrn>? = null

    fun getDtStart(): String? {
        return dtStart
    }

    fun getDtEnd(): String? {
        return dtEnd
    }

    fun getStmtTrnList(): List<StmtTrn>? {
        return stmtTrnList
    }
}
