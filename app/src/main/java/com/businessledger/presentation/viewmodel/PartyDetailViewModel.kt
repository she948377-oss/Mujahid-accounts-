package com.businessledger.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.businessledger.data.local.entity.DisplaySettingsEntity
import com.businessledger.data.repository.LedgerRepository
import com.businessledger.domain.model.Party
import com.businessledger.domain.model.Transaction
import com.businessledger.domain.model.TransactionType
import com.businessledger.domain.usecase.GetPartyLedgerUseCase
import com.businessledger.domain.usecase.PartyLedgerStatement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PartyDetailViewModel(
    private val repository: LedgerRepository,
    private val getPartyLedgerUseCase: GetPartyLedgerUseCase,
    initialPartyId: Long = 0L
) : ViewModel() {

    private val _partyId = MutableStateFlow(initialPartyId)

    val ledgerStatement: StateFlow<PartyLedgerStatement> = _partyId
        .flatMapLatest { id -> getPartyLedgerUseCase(id) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            PartyLedgerStatement(null, emptyList(), 0.0, 0.0, 0.0)
        )

    val displaySettings: StateFlow<DisplaySettingsEntity> = repository.displaySettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DisplaySettingsEntity())

    fun setPartyId(id: Long) {
        _partyId.value = id
    }

    fun addTransaction(
        amount: Double,
        type: TransactionType,
        description: String,
        invoiceNumber: String = "",
        paymentMethod: String = "Cash",
        billImagePath: String? = null,
        date: Long = System.currentTimeMillis()
    ) {
        val currentId = _partyId.value
        if (currentId <= 0) return

        viewModelScope.launch {
            val tx = Transaction(
                partyId = currentId,
                amount = amount,
                type = type,
                description = description.trim(),
                invoiceNumber = invoiceNumber.trim(),
                paymentMethod = paymentMethod.trim().ifEmpty { "Cash" },
                billImagePath = billImagePath,
                date = date
            )
            repository.insertTransaction(tx)
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.updateTransaction(transaction)
        }
    }

    fun deleteTransaction(txId: Long) {
        val currentId = _partyId.value
        viewModelScope.launch {
            repository.deleteTransactionById(txId, currentId)
        }
    }

    fun updateParty(party: Party) {
        viewModelScope.launch {
            repository.updateParty(party)
        }
    }

    class Factory(
        private val repository: LedgerRepository,
        private val useCase: GetPartyLedgerUseCase,
        private val partyId: Long
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PartyDetailViewModel(repository, useCase, partyId) as T
        }
    }
}
