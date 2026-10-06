package uk.skarlet.dojotestapp.feature.transactions.domain

import java.util.Currency
import org.junit.Assert.assertEquals
import org.junit.Test
import uk.skarlet.dojotestapp.core.money.Money
import uk.skarlet.dojotestapp.testing.aTransaction

class SalesSummaryTest {

    @Test
    fun `takings include only completed payments`() {
        val summary = listOf(
            aTransaction(pence = 1_000, status = TransactionStatus.Completed),
            aTransaction(pence = 250, status = TransactionStatus.Completed),
            aTransaction(pence = 9_999, status = TransactionStatus.Refunded),
            aTransaction(pence = 5_000, status = TransactionStatus.Declined),
        ).toSalesSummary(Money.GBP)

        assertEquals(SalesSummary(Money(1_250, Money.GBP), completedCount = 2, declinedCount = 1), summary)
    }

    @Test
    fun `ignores transactions in other currencies`() {
        val eur = Currency.getInstance("EUR")
        val summary = listOf(
            aTransaction(pence = 1_000),
            aTransaction(pence = 500).let { it.copy(amount = Money(500, eur)) },
        ).toSalesSummary(Money.GBP)

        assertEquals(Money(1_000, Money.GBP), summary.takings)
    }

    @Test
    fun `empty list gives zero takings`() {
        assertEquals(SalesSummary(Money.zero(Money.GBP), 0, 0), emptyList<Transaction>().toSalesSummary(Money.GBP))
    }
}
