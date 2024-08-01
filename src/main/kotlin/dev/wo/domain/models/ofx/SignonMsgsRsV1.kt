package dev.wo.domain.models.ofx

import javax.xml.bind.annotation.XmlElement

class SignonMsgsRsV1 {
    @XmlElement(name = "SONRS")
    private val sonRs: SonRs? = null
}
