package com.businessledger.di

import android.content.Context
import com.businessledger.data.local.AppDatabase
import com.businessledger.data.repository.LedgerRepository
import com.businessledger.domain.usecase.GetDashboardSummaryUseCase
import com.businessledger.domain.usecase.GetPartyLedgerUseCase

class AppModule(context: Context) {
    val database: AppDatabase by lazy {
        AppDatabase.getInstance(context)
    }

    val repository: LedgerRepository by lazy {
        LedgerRepository(
            partyDao = database.partyDao(),
            transactionDao = database.transactionDao(),
            productDao = database.productDao(),
            cashEntryDao = database.cashEntryDao(),
            displaySettingsDao = database.displaySettingsDao()
        )
    }

    val getDashboardSummaryUseCase: GetDashboardSummaryUseCase by lazy {
        GetDashboardSummaryUseCase(repository)
    }

    val getPartyLedgerUseCase: GetPartyLedgerUseCase by lazy {
        GetPartyLedgerUseCase(repository)
    }
}
