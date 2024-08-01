package dev.wo.domain.models.ofx

import javax.xml.bind.annotation.XmlElement

class LedgerBal {
    @XmlElement(name = "BALAMT")
    private val balAmt: String? = null

    @XmlElement(name = "DTASOF")
    private val dtAsOf: String? = null
}