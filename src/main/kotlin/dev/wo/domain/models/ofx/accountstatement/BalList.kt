package dev.wo.domain.models.ofx.accountstatement

import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "BALLIST")
class BalList {

    @get:XmlElement(name = "BAL")
    var bal: Bal? = null

}
