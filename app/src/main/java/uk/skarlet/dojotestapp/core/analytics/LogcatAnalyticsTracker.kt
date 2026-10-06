package uk.skarlet.dojotestapp.core.analytics

import android.util.Log
import javax.inject.Inject

class LogcatAnalyticsTracker @Inject constructor() : AnalyticsTracker {
    override fun track(event: AnalyticsEvent) {
        Log.d(TAG, "${event.name} ${event.properties}")
    }

    private companion object {
        const val TAG = "Analytics"
    }
}
