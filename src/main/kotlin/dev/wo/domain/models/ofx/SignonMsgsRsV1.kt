package dev.wo.domain.models.ofx

import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "SIGNONMSGSRSV1")
class SignonMsgsRsV1 {
    @XmlElement(name = "SONRS")
    private val sonRs: SonRs? = null

    fun getSonRs(): SonRs? {
        return sonRs
    }
}
