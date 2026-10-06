package uk.skarlet.dojotestapp.feature.transactions.domain

import java.time.Instant
import uk.skarlet.dojotestapp.core.money.Money

data class Transaction(
    val id: String,
    val amount: Money,
    val status: TransactionStatus,
    val card: CardDetails,
    val createdAt: Instant,
)

enum class TransactionStatus { Completed, Refunded, Declined }

data class CardDetails(val brand: String, val last4: String)
