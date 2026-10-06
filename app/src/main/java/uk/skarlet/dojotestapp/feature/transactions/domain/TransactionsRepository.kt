package uk.skarlet.dojotestapp.feature.transactions.domain

/** Throws [java.io.IOException] for connectivity problems; anything else is unexpected. */
interface TransactionsRepository {
    suspend fun getRecentTransactions(): List<Transaction>
}
