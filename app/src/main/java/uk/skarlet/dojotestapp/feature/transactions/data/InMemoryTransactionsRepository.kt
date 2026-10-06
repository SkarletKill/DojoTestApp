package uk.skarlet.dojotestapp.feature.transactions.data

import java.time.Duration
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import uk.skarlet.dojotestapp.core.coroutines.IoDispatcher
import uk.skarlet.dojotestapp.core.money.Money
import uk.skarlet.dojotestapp.feature.transactions.domain.CardDetails
import uk.skarlet.dojotestapp.feature.transactions.domain.Transaction
import uk.skarlet.dojotestapp.feature.transactions.domain.TransactionStatus
import uk.skarlet.dojotestapp.feature.transactions.domain.TransactionsRepository

/**
 * Stand-in for a network-backed repository so the app runs without a backend.
 * Swapping to Retrofit/Ktor is a one-line change in [TransactionsDataModule].
 */
class InMemoryTransactionsRepository @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : TransactionsRepository {

    override suspend fun getRecentTransactions(): List<Transaction> = withContext(ioDispatcher) {
        delay(SIMULATED_LATENCY_MS)
        seed(now = Instant.now())
    }

    private fun seed(now: Instant): List<Transaction> = listOf(
        tx("tx_008", 1_250, TransactionStatus.Completed, "Visa", "4242", now.minusMinutes(3)),
        tx("tx_007", 480, TransactionStatus.Completed, "Mastercard", "5100", now.minusMinutes(11)),
        tx("tx_006", 3_599, TransactionStatus.Declined, "Amex", "0005", now.minusMinutes(26)),
        tx("tx_005", 2_100, TransactionStatus.Completed, "Visa", "1881", now.minusMinutes(42)),
        tx("tx_004", 975, TransactionStatus.Refunded, "Mastercard", "4444", now.minusMinutes(65)),
        tx("tx_003", 15_000, TransactionStatus.Completed, "Visa", "4242", now.minusMinutes(90)),
        tx("tx_002", 650, TransactionStatus.Declined, "Visa", "0341", now.minusMinutes(130)),
        tx("tx_001", 3_420, TransactionStatus.Completed, "Mastercard", "5100", now.minusMinutes(185)),
    )

    private fun tx(id: String, pence: Long, status: TransactionStatus, brand: String, last4: String, at: Instant) =
        Transaction(id, Money(pence, Money.GBP), status, CardDetails(brand, last4), at)

    private fun Instant.minusMinutes(minutes: Long): Instant = minus(Duration.ofMinutes(minutes))

    private companion object {
        const val SIMULATED_LATENCY_MS = 800L
    }
}
