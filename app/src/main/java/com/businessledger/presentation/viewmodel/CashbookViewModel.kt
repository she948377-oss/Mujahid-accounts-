package com.businessledger.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.businessledger.data.local.entity.DisplaySettingsEntity
import com.businessledger.data.repository.LedgerRepository
import com.businessledger.domain.model.CashEntry
import com.businessledger.domain.model.CashEntryType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

enum class CashbookFilterPeriod {
    TODAY,
    THIS_WEEK,
    THIS_MONTH,
    ALL
}

data class CashbookUiState(
    val entries: List<CashEntry> = emptyList(),
    val period: CashbookFilterPeriod = CashbookFilterPeriod.TODAY,
    val typeFilter: CashEntryType? = null,
    val totalCashIn: Double = 0.0,
    val totalCashOut: Double = 0.0,
    val netCashBalance: Double = 0.0
)

class CashbookViewModel(
    private val repository: LedgerRepository
) : ViewModel() {

    private val _period = MutableStateFlow(CashbookFilterPeriod.TODAY)
    val period: StateFlow<CashbookFilterPeriod> = _period

    private val _typeFilter = MutableStateFlow<CashEntryType?>(null)
    val typeFilter: StateFlow<CashEntryType?> = _typeFilter

    val displaySettings: StateFlow<DisplaySettingsEntity> = repository.displaySettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DisplaySettingsEntity())

    val uiState: StateFlow<CashbookUiState> = combine(
        repository.allCashEntries,
        _period,
        _typeFilter
    ) { allEntries, period, typeFilter ->
        val now = System.currentTimeMillis()
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val startTimestamp = when (period) {
            CashbookFilterPeriod.TODAY -> cal.timeInMillis
            CashbookFilterPeriod.THIS_WEEK -> {
                cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
                cal.timeInMillis
            }
            CashbookFilterPeriod.THIS_MONTH -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.timeInMillis
            }
            CashbookFilterPeriod.ALL -> 0L
        }

        val periodFiltered = allEntries.filter { it.date >= startTimestamp }

        var totalIn = 0.0
        var totalOut = 0.0
        for (e in periodFiltered) {
            if (e.type == CashEntryType.CASH_IN) {
                totalIn += e.amount
            } else {
                totalOut += e.amount
            }
        }

        val typeFiltered = if (typeFilter != null) {
            periodFiltered.filter { it.type == typeFilter }
        } else {
            periodFiltered
        }

        CashbookUiState(
            entries = typeFiltered.sortedByDescending { it.date },
            period = period,
            typeFilter = typeFilter,
            totalCashIn = totalIn,
            totalCashOut = totalOut,
            netCashBalance = totalIn - totalOut
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CashbookUiState())

    fun setPeriod(period: CashbookFilterPeriod) {
        _period.value = period
    }

    fun setTypeFilter(type: CashEntryType?) {
        _typeFilter.value = type
    }

    fun addEntry(
        type: CashEntryType,
        amount: Double,
        category: String,
        description: String,
        paymentMode: String = "Cash",
        date: Long = System.currentTimeMillis()
    ) {
        viewModelScope.launch {
            val entry = CashEntry(
                type = type,
                amount = amount,
                category = category.trim().ifEmpty { if (type == CashEntryType.CASH_IN) "Income" else "Expense" },
                description = description.trim(),
                paymentMode = paymentMode.trim().ifEmpty { "Cash" },
                date = date
            )
            repository.insertCashEntry(entry)
        }
    }

    fun updateEntry(entry: CashEntry) {
        viewModelScope.launch {
            repository.updateCashEntry(entry)
        }
    }

    fun deleteEntry(entryId: Long) {
        viewModelScope.launch {
            repository.deleteCashEntry(entryId)
        }
    }

    class Factory(private val repository: LedgerRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CashbookViewModel(repository) as T
        }
    }
}
