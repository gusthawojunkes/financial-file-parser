package dev.wo.domain.enums

import kotlin.test.Test
import kotlin.test.assertEquals

class FinancialInstitutionTest {

        @Test
        fun `should return Nubank when description is Nubank`() {
            val financialInstitution = FinancialInstitution.fromString("Nubank")
            assertEquals(FinancialInstitution.NUBANK, financialInstitution)
        }

        @Test
        fun `should return Itaú when description is Itaú`() {
            val financialInstitution = FinancialInstitution.fromString("Itaú")
            assertEquals(FinancialInstitution.ITAU, financialInstitution)
        }

        @Test
        fun `should return C6 Bank when description is C6 Bank`() {
            val financialInstitution = FinancialInstitution.fromString("C6 Bank")
            assertEquals(FinancialInstitution.C6_BANK, financialInstitution)
        }

        @Test
        fun `should return UNKNOWN when description is unknown`() {
            val financialInstitution = FinancialInstitution.fromString("unknown")
            assertEquals(FinancialInstitution.UNKNOWN, financialInstitution)
        }

        @Test
        fun `should return UNKNOWN when description is empty`() {
            val financialInstitution = FinancialInstitution.fromString("")
            assertEquals(FinancialInstitution.UNKNOWN, financialInstitution)
        }

        @Test
        fun `should return UNKNOWN when description is not found`() {
            val financialInstitution = FinancialInstitution.fromString("Banco XYZ")
            assertEquals(FinancialInstitution.UNKNOWN, financialInstitution)
        }

    @Test
    fun `should return Any when description is 'Any'`() {
        val financialInstitution = FinancialInstitution.fromString("Any")
        assertEquals(FinancialInstitution.ANY, financialInstitution)
    }

}