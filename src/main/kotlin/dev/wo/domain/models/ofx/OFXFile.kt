package dev.wo.domain.models.ofx

import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "OFX")
class OFXFile {
    @XmlElement(name = "OFXHEADER")
    private val header: String? = null

    @XmlElement(name = "DATA")
    private val data: String? = null

    @XmlElement(name = "VERSION")
    private val version: String? = null

    @XmlElement(name = "SECURITY")
    private val security: String? = null

    @XmlElement(name = "ENCODING")
    private val encoding: String? = null

    @XmlElement(name = "CHARSET")
    private val charset: String? = null

    @XmlElement(name = "COMPRESSION")
    private val compression: String? = null

    @XmlElement(name = "OLDFILEUID")
    private val oldFileUid: String? = null

    @XmlElement(name = "NEWFILEUID")
    private val newFileUid: String? = null

    @XmlElement(name = "SIGNONMSGSRSV1")
    private val signonMsgsRsV1: SignonMsgsRsV1? = null

    @XmlElement(name = "CREDITCARDMSGSRSV1")
    var creditCardMsgsRsV1: CreditCardMsgsRsV1? = null
}
