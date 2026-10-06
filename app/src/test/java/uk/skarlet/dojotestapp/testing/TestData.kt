package uk.skarlet.dojotestapp.testing

import java.time.Instant
import uk.skarlet.dojotestapp.core.money.Money
import uk.skarlet.dojotestapp.feature.transactions.domain.CardDetails
import uk.skarlet.dojotestapp.feature.transactions.domain.Transaction
import uk.skarlet.dojotestapp.feature.transactions.domain.TransactionStatus

fun aTransaction(
    id: String = "tx_1",
    pence: Long = 1_000,
    status: TransactionStatus = TransactionStatus.Completed,
    createdAt: Instant = Instant.parse("2026-10-06T12:00:00Z"),
) = Transaction(
    id = id,
    amount = Money(pence, Money.GBP),
    status = status,
    card = CardDetails("Visa", "4242"),
    createdAt = createdAt,
)
