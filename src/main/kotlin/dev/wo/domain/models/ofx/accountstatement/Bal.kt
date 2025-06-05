package dev.wo.domain.models.ofx.accountstatement

import javax.xml.bind.annotation.XmlElement

class Bal {

    @get:XmlElement(name = "NAME")
    var name: String? = null

    @get:XmlElement(name = "DESC")
    var desc: String? = null

    @get:XmlElement(name = "BALTYPE")
    var balType: String? = null

    @get:XmlElement(name = "VALUE")
    var value: String? = null

}
