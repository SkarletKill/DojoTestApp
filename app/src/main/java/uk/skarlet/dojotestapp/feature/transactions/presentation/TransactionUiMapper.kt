package uk.skarlet.dojotestapp.feature.transactions.presentation

import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import javax.inject.Inject
import kotlinx.collections.immutable.toImmutableList
import uk.skarlet.dojotestapp.core.money.MoneyFormatter
import uk.skarlet.dojotestapp.feature.transactions.domain.SalesSummary
import uk.skarlet.dojotestapp.feature.transactions.domain.Transaction

/** Domain → UI. Keeps formatting (locale, time zone) out of both the ViewModel and composables. */
class TransactionUiMapper(
    private val moneyFormatter: MoneyFormatter,
    private val zoneId: () -> ZoneId,
    private val locale: () -> Locale,
) {

    @Inject
    constructor(moneyFormatter: MoneyFormatter) :
        this(moneyFormatter, { ZoneId.systemDefault() }, { Locale.getDefault() })

    fun toContent(transactions: List<Transaction>, summary: SalesSummary): TransactionsContent {
        val timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
            .withLocale(locale())
            .withZone(zoneId())
        return TransactionsContent(
            summary = SalesSummaryUi(
                takings = moneyFormatter.format(summary.takings),
                completedCount = summary.completedCount,
                declinedCount = summary.declinedCount,
            ),
            transactions = transactions
                .sortedByDescending { it.createdAt }
                .map { tx ->
                    TransactionUi(
                        id = tx.id,
                        cardLabel = "${tx.card.brand} •••• ${tx.card.last4}",
                        time = timeFormatter.format(tx.createdAt),
                        amount = moneyFormatter.format(tx.amount),
                        status = tx.status,
                    )
                }
                .toImmutableList(),
        )
    }
}
