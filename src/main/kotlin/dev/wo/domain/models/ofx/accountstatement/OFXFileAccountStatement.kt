package dev.wo.domain.models.ofx.accountstatement

import dev.wo.domain.models.ofx.CreditCardMsgsRsV1
import dev.wo.domain.models.ofx.SignonMsgsRsV1
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "OFX")
class OFXFileAccountStatement {
    @XmlElement(name = "OFXHEADER")
    private var header: String? = null

    @XmlElement(name = "DATA")
    private var data: String? = null

    @XmlElement(name = "VERSION")
    private var version: String? = null

    @XmlElement(name = "SECURITY")
    private var security: String? = null

    @XmlElement(name = "ENCODING")
    private var encoding: String? = null

    @XmlElement(name = "CHARSET")
    private var charset: String? = null

    @XmlElement(name = "COMPRESSION")
    private var compression: String? = null

    @XmlElement(name = "OLDFILEUID")
    private var oldFileUid: String? = null

    @XmlElement(name = "NEWFILEUID")
    private var newFileUid: String? = null

    @XmlElement(name = "SIGNONMSGSRSV1")
    private var signonMsgsRsV1: SignonMsgsRsV1? = null

    @XmlElement(name = "BANKMSGSRSV1")
    private var bankMsgsRsV1: BankMsgsRsV1? = null

    fun getHeader(): String? {
        return header
    }

    fun getData(): String? {
        return data
    }

    fun getVersion(): String? {
        return version
    }

    fun getSecurity(): String? {
        return security
    }

    fun getEncoding(): String? {
        return encoding
    }

    fun getCharset(): String? {
        return charset
    }

    fun getCompression(): String? {
        return compression
    }

    fun getOldFileUid(): String? {
        return oldFileUid
    }

    fun getNewFileUid(): String? {
        return newFileUid
    }

    fun getSignonMsgsRsV1(): SignonMsgsRsV1? {
        return signonMsgsRsV1
    }

    fun getBankMsgsRsV1(): BankMsgsRsV1? {
        return bankMsgsRsV1
    }
}