package uk.skarlet.dojotestapp.core.money

import java.util.Currency

/**
 * Amounts are stored in minor units (pence) as [Long] — never Float/Double —
 * so sums and refunds are exact. This mirrors how payment APIs represent money.
 */
data class Money(val minorUnits: Long, val currency: Currency) {

    operator fun plus(other: Money): Money {
        require(currency == other.currency) { "Cannot add $currency and ${other.currency}" }
        return Money(minorUnits + other.minorUnits, currency)
    }

    companion object {
        val GBP: Currency = Currency.getInstance("GBP")

        fun zero(currency: Currency) = Money(0, currency)
    }
}
