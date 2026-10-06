package uk.skarlet.dojotestapp.core.money

import java.util.Currency
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Test

class MoneyFormatterTest {

    private val formatter = MoneyFormatter { Locale.UK }

    @Test
    fun `formats minor units as major currency amount`() {
        assertEquals("£12.50", formatter.format(Money(1_250, Money.GBP)))
    }

    @Test
    fun `formats large amounts with grouping`() {
        assertEquals("£1,234,567.89", formatter.format(Money(123_456_789, Money.GBP)))
    }

    @Test
    fun `respects currencies without minor units`() {
        assertEquals("JP¥500", formatter.format(Money(500, Currency.getInstance("JPY"))))
    }
}
