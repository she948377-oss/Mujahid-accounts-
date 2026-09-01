package com.businessledger.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.businessledger.data.local.entity.DisplaySettingsEntity
import com.businessledger.data.repository.LedgerRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: LedgerRepository
) : ViewModel() {

    val settings: StateFlow<DisplaySettingsEntity> = repository.displaySettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DisplaySettingsEntity())

    fun updateBusinessProfile(name: String, phone: String, address: String) {
        viewModelScope.launch {
            val current = settings.value
            repository.updateDisplaySettings(
                current.copy(
                    businessName = name.trim().ifEmpty { "Mujahid Accounts" },
                    businessPhone = phone.trim(),
                    businessAddress = address.trim()
                )
            )
        }
    }

    fun updateCurrency(currencySymbol: String, currencyCode: String = "PKR") {
        viewModelScope.launch {
            val current = settings.value
            repository.updateDisplaySettings(
                current.copy(
                    currencySymbol = currencySymbol,
                    currencyCode = currencyCode
                )
            )
        }
    }

    fun updateLanguage(languageCode: String) {
        viewModelScope.launch {
            val current = settings.value
            val langDisplay = when (languageCode) {
                "ur" -> "Urdu"
                "roman" -> "Roman Urdu"
                "en" -> "English"
                else -> "Bilingual"
            }
            repository.updateDisplaySettings(
                current.copy(
                    language = langDisplay,
                    languageCode = languageCode
                )
            )
        }
    }

    fun updateShowDecimals(show: Boolean) {
        viewModelScope.launch {
            val current = settings.value
            repository.updateDisplaySettings(
                current.copy(
                    showDecimals = show,
                    compactView = !show
                )
            )
        }
    }

    fun updateDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            val current = settings.value
            repository.updateDisplaySettings(
                current.copy(
                    isDarkMode = isDark
                )
            )
        }
    }

    fun updateFullSettings(entity: DisplaySettingsEntity) {
        viewModelScope.launch {
            repository.updateDisplaySettings(entity)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            val allParties = repository.allParties.first()
            for (p in allParties) {
                repository.deleteParty(p.id)
            }
            val allCash = repository.allCashEntries.first()
            for (c in allCash) {
                repository.deleteCashEntry(c.id)
            }
            val allProducts = repository.allProducts.first()
            for (prod in allProducts) {
                repository.deleteProduct(prod.id)
            }
        }
    }

    class Factory(private val repository: LedgerRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(repository) as T
        }
    }
}

