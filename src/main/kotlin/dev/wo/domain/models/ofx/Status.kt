package dev.wo.domain.models.ofx

import javax.xml.bind.annotation.XmlElement

class Status {
    @XmlElement(name = "CODE")
    private val code: String? = null

    @XmlElement(name = "SEVERITY")
    private val severity: String? = null

    fun getCode(): String? {
        return code
    }

    fun getSeverity(): String? {
        return severity
    }
}
