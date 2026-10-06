package uk.skarlet.dojotestapp.feature.transactions.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.collections.immutable.persistentListOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import uk.skarlet.dojotestapp.R
import uk.skarlet.dojotestapp.core.designsystem.AppTheme
import uk.skarlet.dojotestapp.feature.transactions.domain.TransactionStatus

@RunWith(AndroidJUnit4::class)
class TransactionsScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun errorState_retryDispatchesIntent() {
        val intents = mutableListOf<TransactionsIntent>()
        composeRule.setContent {
            AppTheme {
                TransactionsScreen(
                    state = TransactionsState(isLoading = false, errorMessage = R.string.error_generic),
                    onIntent = { intents += it },
                )
            }
        }

        composeRule.onNodeWithText("Try again").performClick()

        assertEquals(listOf(TransactionsIntent.Retry), intents)
    }

    @Test
    fun content_showsTakingsAndTransactions() {
        composeRule.setContent {
            AppTheme {
                TransactionsScreen(
                    state = TransactionsState(
                        isLoading = false,
                        content = TransactionsContent(
                            summary = SalesSummaryUi("£12.50", completedCount = 1, declinedCount = 0),
                            transactions = persistentListOf(
                                TransactionUi("1", "Visa •••• 4242", "14:32", "£12.50", TransactionStatus.Completed),
                            ),
                        ),
                    ),
                    onIntent = {},
                )
            }
        }

        composeRule.onNodeWithText("Visa •••• 4242").assertIsDisplayed()
        composeRule.onNodeWithText("1 payment").assertIsDisplayed()
    }
}
