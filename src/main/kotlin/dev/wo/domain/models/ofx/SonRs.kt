package dev.wo.domain.models.ofx

import javax.xml.bind.annotation.XmlElement

class SonRs {
    @XmlElement(name = "STATUS")
    private val status: Status? = null

    @XmlElement(name = "DTSERVER")
    private val dtServer: String? = null

    @XmlElement(name = "LANGUAGE")
    private val language: String? = null

    fun getStatus(): Status? {
        return status
    }

    fun getDtServer(): String? {
        return dtServer
    }

    fun getLanguage(): String? {
        return language
    }
}
