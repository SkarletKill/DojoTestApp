package uk.skarlet.dojotestapp.feature.transactions.presentation

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import java.io.IOException
import java.time.ZoneOffset
import java.util.Locale
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import uk.skarlet.dojotestapp.R
import uk.skarlet.dojotestapp.core.analytics.AnalyticsTracker
import uk.skarlet.dojotestapp.core.money.MoneyFormatter
import uk.skarlet.dojotestapp.feature.transactions.domain.Transaction
import uk.skarlet.dojotestapp.feature.transactions.domain.TransactionStatus
import uk.skarlet.dojotestapp.feature.transactions.domain.TransactionsRepository
import uk.skarlet.dojotestapp.testing.MainDispatcherRule
import uk.skarlet.dojotestapp.testing.aTransaction

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: TransactionsRepository = mockk()
    private val analytics: AnalyticsTracker = mockk(relaxed = true)
    private val mapper = TransactionUiMapper(
        moneyFormatter = MoneyFormatter { Locale.UK },
        zoneId = { ZoneOffset.UTC },
        locale = { Locale.UK },
    )

    private val transactions = listOf(
        aTransaction(id = "a", pence = 1_250),
        aTransaction(id = "b", pence = 3_000, status = TransactionStatus.Declined),
    )

    private fun createViewModel() = TransactionsViewModel(repository, mapper, analytics)

    @Test
    fun `shows loading until transactions arrive`() = runTest {
        val response = CompletableDeferred<List<Transaction>>()
        coEvery { repository.getRecentTransactions() } coAnswers { response.await() }

        val viewModel = createViewModel()
        advanceUntilIdle()
        assertTrue(viewModel.state.value.isLoading)

        response.complete(transactions)
        advanceUntilIdle()
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `initial load maps transactions and summary`() = runTest {
        coEvery { repository.getRecentTransactions() } returns transactions

        val viewModel = createViewModel()
        advanceUntilIdle()

        val content = viewModel.state.value.content!!
        assertEquals("£12.50", content.summary.takings)
        assertEquals(1, content.summary.completedCount)
        assertEquals(1, content.summary.declinedCount)
        assertEquals(listOf("a", "b"), content.transactions.map { it.id })
        verify {
            analytics.track(match { it.name == "transactions_loaded" && it.properties["trigger"] == "initial" })
        }
    }

    @Test
    fun `offline on first load shows full-screen connectivity error`() = runTest {
        coEvery { repository.getRecentTransactions() } throws IOException()

        val viewModel = createViewModel()
        advanceUntilIdle()

        with(viewModel.state.value) {
            assertFalse(isLoading)
            assertNull(content)
            assertEquals(R.string.error_no_connection, errorMessage)
        }
        verify {
            analytics.track(match { it.name == "transactions_load_failed" && it.properties["reason"] == "network" })
        }
    }

    @Test
    fun `retry after failure recovers`() = runTest {
        coEvery { repository.getRecentTransactions() } throws IllegalStateException() andThen transactions

        val viewModel = createViewModel()
        advanceUntilIdle()
        assertEquals(R.string.error_generic, viewModel.state.value.errorMessage)

        viewModel.onIntent(TransactionsIntent.Retry)
        advanceUntilIdle()

        assertNull(viewModel.state.value.errorMessage)
        assertEquals(2, viewModel.state.value.content?.transactions?.size)
    }

    @Test
    fun `failed refresh keeps existing content and shows a message`() = runTest {
        coEvery { repository.getRecentTransactions() } returns transactions andThenThrows IOException()

        val viewModel = createViewModel()
        advanceUntilIdle()
        val contentBefore = viewModel.state.value.content

        viewModel.effects.test {
            viewModel.onIntent(TransactionsIntent.Refresh)
            assertEquals(TransactionsEffect.ShowMessage(R.string.error_no_connection), awaitItem())
        }

        with(viewModel.state.value) {
            assertEquals(contentBefore, content)
            assertFalse(isRefreshing)
            assertNull(errorMessage)
        }
    }

    @Test
    fun `ignores refresh while a load is already in flight`() = runTest {
        coEvery { repository.getRecentTransactions() } returns transactions

        val viewModel = createViewModel()
        viewModel.onIntent(TransactionsIntent.Refresh) // initial load not finished yet
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.getRecentTransactions() }
    }
}
