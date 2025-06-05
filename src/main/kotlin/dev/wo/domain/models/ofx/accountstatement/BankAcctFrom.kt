package dev.wo.domain.models.ofx.accountstatement

import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "BANKACCTFROM")
class BankAcctFrom {

    @get:XmlElement(name = "BANKID")
    var bankId: String? = null

    @get:XmlElement(name = "BRANCHID")
    var branchId: String? = null

    @get:XmlElement(name = "ACCTID")
    var acctId: String? = null

    @get:XmlElement(name = "ACCTTYPE")
    var acctType: String? = null

}