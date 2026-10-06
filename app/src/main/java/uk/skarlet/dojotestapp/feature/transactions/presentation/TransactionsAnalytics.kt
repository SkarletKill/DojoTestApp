package uk.skarlet.dojotestapp.feature.transactions.presentation

import uk.skarlet.dojotestapp.core.analytics.AnalyticsEvent

/**
 * Events that answer product questions, not just "screen opened":
 * - How long until a merchant sees their money? (time-to-content, by trigger)
 * - How often does loading fail, and why? (failure rate, connectivity vs. bug)
 * - Do merchants rely on pull-to-refresh? (signals whether we need live updates)
 */
internal enum class LoadTrigger(val value: String) { Initial("initial"), Retry("retry"), Refresh("refresh") }

internal object TransactionsAnalytics {
    fun loaded(trigger: LoadTrigger, count: Int, declinedCount: Int, durationMs: Long) = AnalyticsEvent(
        name = "transactions_loaded",
        properties = mapOf(
            "trigger" to trigger.value,
            "count" to count,
            "declined_count" to declinedCount,
            "duration_ms" to durationMs,
        ),
    )

    fun loadFailed(trigger: LoadTrigger, reason: String) = AnalyticsEvent(
        name = "transactions_load_failed",
        properties = mapOf("trigger" to trigger.value, "reason" to reason),
    )
}
