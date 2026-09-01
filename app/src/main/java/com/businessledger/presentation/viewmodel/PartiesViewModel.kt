package com.businessledger.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.businessledger.data.local.entity.DisplaySettingsEntity
import com.businessledger.data.repository.LedgerRepository
import com.businessledger.domain.model.Party
import com.businessledger.domain.model.PartyType
import com.businessledger.domain.model.Transaction
import com.businessledger.domain.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class PartyFilterTab {
    ALL,
    CUSTOMERS,
    SUPPLIERS
}

enum class PartySortOption {
    RECENT_UPDATED,
    NAME_ASC,
    HIGHEST_RECEIVABLE,
    HIGHEST_PAYABLE
}

data class PartiesUiState(
    val parties: List<Party> = emptyList(),
    val filterTab: PartyFilterTab = PartyFilterTab.ALL,
    val searchQuery: String = "",
    val sortOption: PartySortOption = PartySortOption.RECENT_UPDATED,
    val totalReceivable: Double = 0.0,
    val totalPayable: Double = 0.0
)

class PartiesViewModel(
    private val repository: LedgerRepository
) : ViewModel() {

    private val _filterTab = MutableStateFlow(PartyFilterTab.ALL)
    val filterTab: StateFlow<PartyFilterTab> = _filterTab

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _sortOption = MutableStateFlow(PartySortOption.RECENT_UPDATED)
    val sortOption: StateFlow<PartySortOption> = _sortOption

    val displaySettings: StateFlow<DisplaySettingsEntity> = repository.displaySettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DisplaySettingsEntity())

    val uiState: StateFlow<PartiesUiState> = combine(
        repository.allParties,
        _filterTab,
        _searchQuery,
        _sortOption
    ) { allParties, tab, query, sort ->
        val filteredByType = when (tab) {
            PartyFilterTab.ALL -> allParties
            PartyFilterTab.CUSTOMERS -> allParties.filter { it.partyType == PartyType.CUSTOMER }
            PartyFilterTab.SUPPLIERS -> allParties.filter { it.partyType == PartyType.SUPPLIER }
        }

        val filteredBySearch = if (query.isBlank()) {
            filteredByType
        } else {
            filteredByType.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.phone.contains(query, ignoreCase = true) ||
                it.address.contains(query, ignoreCase = true)
            }
        }

        val sorted = when (sort) {
            PartySortOption.RECENT_UPDATED -> filteredBySearch.sortedByDescending { it.updatedAt }
            PartySortOption.NAME_ASC -> filteredBySearch.sortedBy { it.name.lowercase() }
            PartySortOption.HIGHEST_RECEIVABLE -> filteredBySearch.sortedByDescending { it.currentBalance }
            PartySortOption.HIGHEST_PAYABLE -> filteredBySearch.sortedBy { it.currentBalance }
        }

        var receivable = 0.0
        var payable = 0.0
        for (p in allParties) {
            if (p.currentBalance > 0) receivable += p.currentBalance
            else if (p.currentBalance < 0) payable += Math.abs(p.currentBalance)
        }

        PartiesUiState(
            parties = sorted,
            filterTab = tab,
            searchQuery = query,
            sortOption = sort,
            totalReceivable = receivable,
            totalPayable = payable
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PartiesUiState())

    fun setFilterTab(tab: PartyFilterTab) {
        _filterTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSortOption(sort: PartySortOption) {
        _sortOption.value = sort
    }

    fun addParty(
        name: String,
        phone: String,
        address: String,
        partyType: PartyType,
        openingBalance: Double,
        notes: String
    ) {
        viewModelScope.launch {
            val party = Party(
                name = name.trim(),
                phone = phone.trim(),
                address = address.trim(),
                partyType = partyType,
                openingBalance = openingBalance,
                currentBalance = openingBalance,
                notes = notes.trim()
            )
            repository.insertParty(party)
        }
    }

    fun updateParty(party: Party) {
        viewModelScope.launch {
            repository.updateParty(party)
        }
    }

    fun deleteParty(partyId: Long) {
        viewModelScope.launch {
            repository.deleteParty(partyId)
        }
    }

    fun recordQuickTransaction(partyId: Long, amount: Double, type: TransactionType, note: String) {
        viewModelScope.launch {
            val tx = Transaction(
                partyId = partyId,
                amount = amount,
                type = type,
                description = note.trim()
            )
            repository.insertTransaction(tx)
        }
    }

    class Factory(private val repository: LedgerRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PartiesViewModel(repository) as T
        }
    }
}
