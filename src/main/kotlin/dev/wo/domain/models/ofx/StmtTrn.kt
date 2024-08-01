package dev.wo.domain.models.ofx

import javax.xml.bind.annotation.XmlElement

class StmtTrn {
    @XmlElement(name = "TRNTYPE")
    var trnType: String? = null

    @XmlElement(name = "DTPOSTED")
    var dtPosted: String? = null

    @XmlElement(name = "TRNAMT")
    var trnAmt: String? = null

    @XmlElement(name = "FITID")
    var fitId: String? = null

    @XmlElement(name = "MEMO")
    var memo: String? = null
}