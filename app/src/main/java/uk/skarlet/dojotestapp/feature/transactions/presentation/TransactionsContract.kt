package uk.skarlet.dojotestapp.feature.transactions.presentation

import androidx.annotation.StringRes
import kotlinx.collections.immutable.ImmutableList
import uk.skarlet.dojotestapp.feature.transactions.domain.TransactionStatus

data class TransactionsState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val content: TransactionsContent? = null,
    /** Full-screen error — only shown when there's nothing on screen yet. */
    @StringRes val errorMessage: Int? = null,
)

data class TransactionsContent(
    val summary: SalesSummaryUi,
    val transactions: ImmutableList<TransactionUi>,
)

data class SalesSummaryUi(
    val takings: String,
    val completedCount: Int,
    val declinedCount: Int,
)

data class TransactionUi(
    val id: String,
    val cardLabel: String,
    val time: String,
    val amount: String,
    val status: TransactionStatus,
)

sealed interface TransactionsIntent {
    data object Retry : TransactionsIntent
    data object Refresh : TransactionsIntent
}

sealed interface TransactionsEffect {
    data class ShowMessage(@StringRes val message: Int) : TransactionsEffect
}
