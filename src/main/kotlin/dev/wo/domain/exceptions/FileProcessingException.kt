package dev.wo.domain.exceptions

import dev.wo.domain.enums.FinancialInstitution
import io.ktor.http.HttpStatusCode

class FileProcessingException(
    override val status: HttpStatusCode,
    override val message: String,
    val institution: FinancialInstitution,
    val fileType: String
) : HttpException(status, message) {

    constructor(
        message: String,
        institution: FinancialInstitution,
        fileType: String
    ) : this(HttpStatusCode.BadRequest, message, institution, fileType)
}