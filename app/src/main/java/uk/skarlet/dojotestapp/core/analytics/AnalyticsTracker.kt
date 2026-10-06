package uk.skarlet.dojotestapp.core.analytics

/**
 * Product analytics seam. Features describe *what happened*; the implementation decides
 * *where it goes* (Logcat today; Amplitude/Firebase/Segment later without touching features).
 */
interface AnalyticsTracker {
    fun track(event: AnalyticsEvent)
}

data class AnalyticsEvent(
    val name: String,
    val properties: Map<String, Any> = emptyMap(),
)
