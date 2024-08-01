package dev.wo.domain.models.ofx

import javax.xml.bind.annotation.XmlElement

class CcAcctFrom {
    @XmlElement(name = "ACCTID")
    private val acctId: String? = null
}