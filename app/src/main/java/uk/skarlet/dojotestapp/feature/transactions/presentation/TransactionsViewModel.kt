package uk.skarlet.dojotestapp.feature.transactions.presentation

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.TimeSource
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import uk.skarlet.dojotestapp.R
import uk.skarlet.dojotestapp.core.analytics.AnalyticsTracker
import uk.skarlet.dojotestapp.core.money.Money
import uk.skarlet.dojotestapp.core.mvi.BaseViewModel
import uk.skarlet.dojotestapp.feature.transactions.domain.TransactionsRepository
import uk.skarlet.dojotestapp.feature.transactions.domain.toSalesSummary

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val repository: TransactionsRepository,
    private val mapper: TransactionUiMapper,
    private val analytics: AnalyticsTracker,
) : BaseViewModel<TransactionsState, TransactionsIntent, TransactionsEffect>(TransactionsState()) {

    private var loadJob: Job? = null

    init {
        load(LoadTrigger.Initial)
    }

    override fun onIntent(intent: TransactionsIntent) {
        when (intent) {
            TransactionsIntent.Retry -> load(LoadTrigger.Retry)
            TransactionsIntent.Refresh -> load(LoadTrigger.Refresh)
        }
    }

    private fun load(trigger: LoadTrigger) {
        if (loadJob?.isActive == true) return // de-dupe rapid retries / refreshes

        loadJob = viewModelScope.launch {
            val hasContent = currentState.content != null
            setState { copy(isLoading = !hasContent, isRefreshing = hasContent, errorMessage = null) }
            val started = TimeSource.Monotonic.markNow()

            try {
                val transactions = repository.getRecentTransactions()
                // Single-currency merchant for now; multi-currency would group summaries per currency.
                val summary = transactions.toSalesSummary(Money.GBP)
                setState {
                    copy(isLoading = false, isRefreshing = false, content = mapper.toContent(transactions, summary))
                }
                analytics.track(
                    TransactionsAnalytics.loaded(
                        trigger = trigger,
                        count = transactions.size,
                        declinedCount = summary.declinedCount,
                        durationMs = started.elapsedNow().inWholeMilliseconds,
                    ),
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                onLoadFailed(trigger, e, hasContent)
            }
        }
    }

    private fun onLoadFailed(trigger: LoadTrigger, error: Exception, hadContent: Boolean) {
        val message = if (error is IOException) R.string.error_no_connection else R.string.error_generic
        if (hadContent) {
            // Never wipe data the merchant can already see — keep it and tell them it's stale.
            setState { copy(isRefreshing = false) }
            sendEffect(TransactionsEffect.ShowMessage(message))
        } else {
            setState { copy(isLoading = false, errorMessage = message) }
        }
        analytics.track(
            TransactionsAnalytics.loadFailed(
                trigger = trigger,
                reason = if (error is IOException) "network" else error::class.simpleName ?: "unknown",
            ),
        )
    }
}
