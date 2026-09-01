package com.businessledger.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.businessledger.data.local.entity.DisplaySettingsEntity
import com.businessledger.data.repository.LedgerRepository
import com.businessledger.domain.model.CashEntry
import com.businessledger.domain.model.CashEntryType
import com.businessledger.domain.model.Party
import com.businessledger.domain.model.PartyType
import com.businessledger.domain.model.Product
import com.businessledger.domain.model.Transaction
import com.businessledger.domain.model.TransactionType
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

    fun populateSampleData() {
        viewModelScope.launch {
            // Sample Customers & Suppliers
            val p1 = Party(name = "Ali Traders (علی ٹریڈرز)", phone = "0300-1234567", address = "Shop #12, Anarkali, Lahore", partyType = PartyType.CUSTOMER, openingBalance = 45000.0, currentBalance = 45000.0)
            val p2 = Party(name = "Usman Cloth House (عثمان کلاتھ)", phone = "0321-7654321", address = "Main Bazaar, Faisalabad", partyType = PartyType.CUSTOMER, openingBalance = 18500.0, currentBalance = 18500.0)
            val p3 = Party(name = "Madina Wholesale Mills (مدینہ ملز)", phone = "0333-9876543", address = "Industrial Area, Gujranwala", partyType = PartyType.SUPPLIER, openingBalance = -85000.0, currentBalance = -85000.0)
            val p4 = Party(name = "Tariq Packages & Cartons", phone = "0345-1122334", address = "SITE Area, Karachi", partyType = PartyType.SUPPLIER, openingBalance = -12000.0, currentBalance = -12000.0)

            val p1Id = repository.insertParty(p1)
            val p2Id = repository.insertParty(p2)
            val p3Id = repository.insertParty(p3)
            val p4Id = repository.insertParty(p4)

            // Transactions
            repository.insertTransaction(Transaction(partyId = p1Id, amount = 25000.0, type = TransactionType.GAVE, description = "Cotton Fabric 100 meters (بل # 104)", invoiceNumber = "104"))
            repository.insertTransaction(Transaction(partyId = p1Id, amount = 10000.0, type = TransactionType.GOT, description = "Received Cash on Counter (وصولی نقد)"))
            repository.insertTransaction(Transaction(partyId = p2Id, amount = 15000.0, type = TransactionType.GAVE, description = "Silk Suits 20 Pcs"))
            repository.insertTransaction(Transaction(partyId = p3Id, amount = 50000.0, type = TransactionType.GOT, description = "Paid via Bank Transfer"))

            // Sample Cashbook Entries
            repository.insertCashEntry(CashEntry(type = CashEntryType.CASH_IN, amount = 35000.0, category = "Counter Sales", description = "Daily Cash counter sales"))
            repository.insertCashEntry(CashEntry(type = CashEntryType.CASH_IN, amount = 12000.0, category = "Direct Payment", description = "Cash received from retail client"))
            repository.insertCashEntry(CashEntry(type = CashEntryType.CASH_OUT, amount = 2500.0, category = "Electricity Bill", description = "Shop electric bill paid"))
            repository.insertCashEntry(CashEntry(type = CashEntryType.CASH_OUT, amount = 850.0, category = "Tea / Food", description = "Guest refreshment & tea"))
            repository.insertCashEntry(CashEntry(type = CashEntryType.CASH_OUT, amount = 15000.0, category = "Shop Rent", description = "Shop monthly rent"))

            // Sample Products
            repository.insertProduct(Product(name = "Premium Cotton Fabric", sku = "COT-01", purchasePrice = 450.0, sellingPrice = 650.0, stockQuantity = 120.0, unit = "Mtr", category = "Fabric", minStockAlert = 20.0))
            repository.insertProduct(Product(name = "Lawn Printed Suits 3pc", sku = "LWN-3PC", purchasePrice = 1800.0, sellingPrice = 2800.0, stockQuantity = 45.0, unit = "Suits", category = "Garments", minStockAlert = 10.0))
            repository.insertProduct(Product(name = "Silk Embroidered Dupatta", sku = "SLK-DUP", purchasePrice = 800.0, sellingPrice = 1350.0, stockQuantity = 4.0, unit = "Pcs", category = "Accessories", minStockAlert = 8.0))
            repository.insertProduct(Product(name = "Packing Corrugated Boxes", sku = "BOX-LG", purchasePrice = 85.0, sellingPrice = 120.0, stockQuantity = 200.0, unit = "Boxes", category = "Packaging", minStockAlert = 50.0))
        }
    }

    class Factory(private val repository: LedgerRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(repository) as T
        }
    }
}
