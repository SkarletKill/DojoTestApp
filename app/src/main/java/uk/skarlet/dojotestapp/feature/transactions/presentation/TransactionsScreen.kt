package uk.skarlet.dojotestapp.feature.transactions.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.persistentListOf
import uk.skarlet.dojotestapp.R
import uk.skarlet.dojotestapp.core.designsystem.AppTheme
import uk.skarlet.dojotestapp.core.mvi.CollectEffects
import uk.skarlet.dojotestapp.feature.transactions.domain.TransactionStatus

/** Stateful entry point: wires the ViewModel. Everything below it is stateless and previewable. */
@Composable
fun TransactionsRoute(viewModel: TransactionsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current

    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            is TransactionsEffect.ShowMessage -> snackbarHostState.showSnackbar(resources.getString(effect.message))
        }
    }

    TransactionsScreen(state = state, onIntent = viewModel::onIntent, snackbarHostState = snackbarHostState)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    state: TransactionsState,
    onIntent: (TransactionsIntent) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text(stringResource(R.string.transactions_title)) }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                state.content != null -> PullToRefreshBox(
                    isRefreshing = state.isRefreshing,
                    onRefresh = { onIntent(TransactionsIntent.Refresh) },
                ) {
                    TransactionsContent(state.content)
                }
                state.errorMessage != null -> ErrorState(
                    message = stringResource(state.errorMessage),
                    onRetry = { onIntent(TransactionsIntent.Retry) },
                )
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
private fun TransactionsContent(content: TransactionsContent) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        item(key = "summary") { SummaryCard(content.summary) }
        if (content.transactions.isEmpty()) {
            item(key = "empty") { EmptyState() }
        } else {
            item(key = "header") {
                Text(
                    text = stringResource(R.string.transactions_recent_header),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .semantics { heading() },
                )
            }
            items(content.transactions, key = { it.id }) { tx ->
                TransactionRow(tx)
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun SummaryCard(summary: SalesSummaryUi) {
    Card(Modifier.fillMaxWidth().padding(16.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text(stringResource(R.string.transactions_summary_label), style = MaterialTheme.typography.labelLarge)
            Text(summary.takings, style = MaterialTheme.typography.displaySmall)
            Spacer(Modifier.height(4.dp))
            Text(
                text = pluralStringResource(R.plurals.transactions_completed_count, summary.completedCount, summary.completedCount),
                style = MaterialTheme.typography.bodyMedium,
            )
            if (summary.declinedCount > 0) {
                Text(
                    text = pluralStringResource(R.plurals.transactions_declined_count, summary.declinedCount, summary.declinedCount),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
private fun TransactionRow(tx: TransactionUi) {
    val statusLabel = when (tx.status) {
        TransactionStatus.Completed -> null
        TransactionStatus.Refunded -> stringResource(R.string.transactions_status_refunded)
        TransactionStatus.Declined -> stringResource(R.string.transactions_status_declined)
    }
    ListItem(
        headlineContent = { Text(tx.cardLabel) },
        supportingContent = { Text(listOfNotNull(tx.time, statusLabel).joinToString(" · ")) },
        trailingContent = {
            Text(
                text = tx.amount,
                style = MaterialTheme.typography.titleMedium,
                // Money that didn't land is struck through so it can't be mistaken for takings.
                textDecoration = if (tx.status == TransactionStatus.Completed) null else TextDecoration.LineThrough,
                color = if (tx.status == TransactionStatus.Declined) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
        },
    )
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(stringResource(R.string.transactions_empty_title), style = MaterialTheme.typography.titleMedium)
        Text(
            text = stringResource(R.string.transactions_empty_body),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    ) {
        Text(message, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
        Button(onClick = onRetry) { Text(stringResource(R.string.action_retry)) }
    }
}

// region Previews

private val previewContent = TransactionsContent(
    summary = SalesSummaryUi(takings = "£221.70", completedCount = 5, declinedCount = 2),
    transactions = persistentListOf(
        TransactionUi("1", "Visa •••• 4242", "14:32", "£12.50", TransactionStatus.Completed),
        TransactionUi("2", "Amex •••• 0005", "14:09", "£35.99", TransactionStatus.Declined),
        TransactionUi("3", "Mastercard •••• 4444", "13:30", "£9.75", TransactionStatus.Refunded),
    ),
)

@Preview(showBackground = true)
@Composable
private fun ContentPreview() = AppTheme {
    TransactionsScreen(TransactionsState(isLoading = false, content = previewContent), onIntent = {})
}

@Preview(showBackground = true)
@Composable
private fun EmptyPreview() = AppTheme {
    TransactionsScreen(
        TransactionsState(isLoading = false, content = previewContent.copy(transactions = persistentListOf())),
        onIntent = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun ErrorPreview() = AppTheme {
    TransactionsScreen(TransactionsState(isLoading = false, errorMessage = R.string.error_no_connection), onIntent = {})
}

// endregion
