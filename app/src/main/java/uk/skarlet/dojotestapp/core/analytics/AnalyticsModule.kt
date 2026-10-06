package uk.skarlet.dojotestapp.core.analytics

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AnalyticsModule {
    @Binds
    @Singleton
    fun bindAnalyticsTracker(impl: LogcatAnalyticsTracker): AnalyticsTracker
}
