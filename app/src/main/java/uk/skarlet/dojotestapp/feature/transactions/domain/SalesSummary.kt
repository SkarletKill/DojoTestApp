package uk.skarlet.dojotestapp.feature.transactions.domain

import java.util.Currency
import uk.skarlet.dojotestapp.core.money.Money

/**
 * What a merchant actually wants to know at a glance: "how much have I taken, and is anything going wrong?"
 * Declines are surfaced explicitly because each one is potentially lost revenue.
 */
data class SalesSummary(
    val takings: Money,
    val completedCount: Int,
    val declinedCount: Int,
)

fun List<Transaction>.toSalesSummary(currency: Currency): SalesSummary {
    val inCurrency = filter { it.amount.currency == currency }
    val completed = inCurrency.filter { it.status == TransactionStatus.Completed }
    return SalesSummary(
        takings = completed.fold(Money.zero(currency)) { total, tx -> total + tx.amount },
        completedCount = completed.size,
        declinedCount = inCurrency.count { it.status == TransactionStatus.Declined },
    )
}
