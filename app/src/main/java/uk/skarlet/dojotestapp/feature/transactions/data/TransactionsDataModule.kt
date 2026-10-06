package uk.skarlet.dojotestapp.feature.transactions.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import uk.skarlet.dojotestapp.feature.transactions.domain.TransactionsRepository

@Module
@InstallIn(SingletonComponent::class)
interface TransactionsDataModule {
    @Binds
    @Singleton
    fun bindTransactionsRepository(impl: InMemoryTransactionsRepository): TransactionsRepository
}
