package com.businessledger.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.businessledger.data.local.entity.DisplaySettingsEntity
import com.businessledger.data.repository.LedgerRepository
import com.businessledger.domain.model.CashEntry
import com.businessledger.domain.model.CashEntryType
import com.businessledger.domain.model.DashboardSummary
import com.businessledger.domain.model.Party
import com.businessledger.domain.model.PartyType
import com.businessledger.domain.model.Transaction
import com.businessledger.domain.model.TransactionType
import com.businessledger.domain.usecase.GetDashboardSummaryUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: LedgerRepository,
    private val getDashboardSummaryUseCase: GetDashboardSummaryUseCase
) : ViewModel() {

    val summaryState: StateFlow<DashboardSummary> = getDashboardSummaryUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardSummary())

    val recentTransactions: StateFlow<List<Transaction>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val parties: StateFlow<List<Party>> = repository.allParties
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val displaySettings: StateFlow<DisplaySettingsEntity> = repository.displaySettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DisplaySettingsEntity())

    fun quickAddParty(name: String, phone: String, partyType: PartyType, openingBalance: Double) {
        viewModelScope.launch {
            val party = Party(
                name = name.trim(),
                phone = phone.trim(),
                partyType = partyType,
                openingBalance = openingBalance,
                currentBalance = openingBalance
            )
            repository.insertParty(party)
        }
    }

    fun quickAddCashEntry(type: CashEntryType, amount: Double, category: String, description: String) {
        viewModelScope.launch {
            val entry = CashEntry(
                type = type,
                amount = amount,
                category = category.trim().ifEmpty { if (type == CashEntryType.CASH_IN) "Income" else "Expense" },
                description = description.trim()
            )
            repository.insertCashEntry(entry)
        }
    }

    fun quickAddTransaction(partyId: Long, amount: Double, type: TransactionType, description: String) {
        viewModelScope.launch {
            val tx = Transaction(
                partyId = partyId,
                amount = amount,
                type = type,
                description = description.trim()
            )
            repository.insertTransaction(tx)
        }
    }

    class Factory(
        private val repository: LedgerRepository,
        private val useCase: GetDashboardSummaryUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DashboardViewModel(repository, useCase) as T
        }
    }
}
