package dev.wo.domain.models.ofx

import javax.xml.bind.annotation.XmlElement

class StmtTrn {
    @XmlElement(name = "TRNTYPE")
    private var trnType: String? = null

    @XmlElement(name = "DTPOSTED")
    private var dtPosted: String? = null

    @XmlElement(name = "TRNAMT")
    private var trnAmt: String? = null

    @XmlElement(name = "FITID")
    private var fitId: String? = null

    @XmlElement(name = "MEMO")
    private var memo: String? = null

    fun getTrnType(): String? {
        return trnType
    }

    fun getDtPosted(): String? {
        return dtPosted
    }

    fun getTrnAmt(): String? {
        return trnAmt
    }

    fun getFitId(): String? {
        return fitId
    }

    fun getMemo(): String? {
        return memo
    }
}