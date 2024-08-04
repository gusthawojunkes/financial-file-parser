package dev.wo.domain.enums

import kotlin.test.Test


class CardTypeTest {

    @Test
    fun `should return credit card type`() {
        val cardType = CardType.fromString("credit")
        assert(cardType == CardType.CREDIT)
    }

    @Test
    fun `should return debit card type`() {
        val cardType = CardType.fromString("debit")
        assert(cardType == CardType.DEBIT)
    }

    @Test
    fun `should return none card type`() {
        val cardType = CardType.fromString("none")
        assert(cardType == CardType.NONE)
    }

    @Test
    fun `should return none card type when invalid`() {
        val cardType = CardType.fromString("invalid")
        assert(cardType == CardType.NONE)
    }

}