package com.businessledger.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.businessledger.data.local.entity.DisplaySettingsEntity
import com.businessledger.data.repository.LedgerRepository
import com.businessledger.domain.model.CashEntry
import com.businessledger.domain.model.CashEntryType
import com.businessledger.domain.model.Party
import com.businessledger.domain.model.Product
import com.businessledger.domain.model.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

enum class ReportType {
    BALANCE_SHEET,
    RECEIVABLE_LIST,
    PAYABLE_LIST,
    CASH_FLOW_SUMMARY,
    INVENTORY_VALUATION
}

data class ReportsUiState(
    val reportType: ReportType = ReportType.BALANCE_SHEET,
    val selectedPeriodDays: Int = 30, // 7, 30, 90, 365
    val totalReceivable: Double = 0.0,
    val totalPayable: Double = 0.0,
    val netWorth: Double = 0.0,
    val stockValuation: Double = 0.0,
    val totalCashIn: Double = 0.0,
    val totalCashOut: Double = 0.0,
    val netCashFlow: Double = 0.0,
    val debtorsList: List<Party> = emptyList(),
    val creditorsList: List<Party> = emptyList(),
    val recentTransactions: List<Transaction> = emptyList(),
    val lowStockProducts: List<Product> = emptyList(),
    val allProducts: List<Product> = emptyList()
)

private data class BusinessEntitiesBundle(
    val parties: List<Party>,
    val transactions: List<Transaction>,
    val cashEntries: List<CashEntry>,
    val products: List<Product>
)

class ReportsViewModel(
    private val repository: LedgerRepository
) : ViewModel() {

    private val _reportType = MutableStateFlow(ReportType.BALANCE_SHEET)
    val reportType: StateFlow<ReportType> = _reportType

    private val _selectedPeriodDays = MutableStateFlow(30)
    val selectedPeriodDays: StateFlow<Int> = _selectedPeriodDays

    val displaySettings: StateFlow<DisplaySettingsEntity> = repository.displaySettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DisplaySettingsEntity())

    private val entitiesBundle = combine(
        repository.allParties,
        repository.allTransactions,
        repository.allCashEntries,
        repository.allProducts
    ) { parties, txs, cash, prods ->
        BusinessEntitiesBundle(parties, txs, cash, prods)
    }

    val uiState: StateFlow<ReportsUiState> = combine(
        entitiesBundle,
        _reportType,
        _selectedPeriodDays
    ) { bundle, repType, days ->
        var totalReceivable = 0.0
        var totalPayable = 0.0
        val debtors = mutableListOf<Party>()
        val creditors = mutableListOf<Party>()

        for (p in bundle.parties) {
            if (p.currentBalance > 0) {
                totalReceivable += p.currentBalance
                debtors.add(p)
            } else if (p.currentBalance < 0) {
                totalPayable += Math.abs(p.currentBalance)
                creditors.add(p)
            }
        }

        var stockValuation = 0.0
        val lowStock = mutableListOf<Product>()
        for (prod in bundle.products) {
            stockValuation += (prod.purchasePrice * prod.stockQuantity)
            if (prod.stockQuantity <= prod.minStockAlert) {
                lowStock.add(prod)
            }
        }

        val cutoffTimestamp = System.currentTimeMillis() - (days.toLong() * 86400000L)
        val periodCash = bundle.cashEntries.filter { it.date >= cutoffTimestamp }
        var cashIn = 0.0
        var cashOut = 0.0
        for (c in periodCash) {
            if (c.type == CashEntryType.CASH_IN) cashIn += c.amount
            else cashOut += c.amount
        }

        val periodTxs = bundle.transactions.filter { it.date >= cutoffTimestamp }

        ReportsUiState(
            reportType = repType,
            selectedPeriodDays = days,
            totalReceivable = totalReceivable,
            totalPayable = totalPayable,
            netWorth = (totalReceivable + stockValuation + (cashIn - cashOut)) - totalPayable,
            stockValuation = stockValuation,
            totalCashIn = cashIn,
            totalCashOut = cashOut,
            netCashFlow = cashIn - cashOut,
            debtorsList = debtors.sortedByDescending { it.currentBalance },
            creditorsList = creditors.sortedBy { it.currentBalance },
            recentTransactions = periodTxs.sortedByDescending { it.date },
            lowStockProducts = lowStock,
            allProducts = bundle.products
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ReportsUiState())

    fun setReportType(type: ReportType) {
        _reportType.value = type
    }

    fun setPeriodDays(days: Int) {
        _selectedPeriodDays.value = days
    }

    class Factory(private val repository: LedgerRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ReportsViewModel(repository) as T
        }
    }
}
