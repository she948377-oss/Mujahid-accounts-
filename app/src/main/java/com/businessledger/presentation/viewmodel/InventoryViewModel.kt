package com.businessledger.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.businessledger.data.local.entity.DisplaySettingsEntity
import com.businessledger.data.repository.LedgerRepository
import com.businessledger.domain.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class InventoryFilterTab {
    ALL_PRODUCTS,
    LOW_STOCK
}

data class InventoryUiState(
    val products: List<Product> = emptyList(),
    val filterTab: InventoryFilterTab = InventoryFilterTab.ALL_PRODUCTS,
    val searchQuery: String = "",
    val totalItemsCount: Int = 0,
    val lowStockCount: Int = 0,
    val totalStockValuation: Double = 0.0
)

class InventoryViewModel(
    private val repository: LedgerRepository
) : ViewModel() {

    private val _filterTab = MutableStateFlow(InventoryFilterTab.ALL_PRODUCTS)
    val filterTab: StateFlow<InventoryFilterTab> = _filterTab

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val displaySettings: StateFlow<DisplaySettingsEntity> = repository.displaySettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DisplaySettingsEntity())

    val uiState: StateFlow<InventoryUiState> = combine(
        repository.allProducts,
        _filterTab,
        _searchQuery
    ) { allProducts, tab, query ->
        var lowStock = 0
        var totalValuation = 0.0

        for (p in allProducts) {
            if (p.stockQuantity <= p.minStockAlert) {
                lowStock++
            }
            totalValuation += (p.purchasePrice * p.stockQuantity)
        }

        val filteredByTab = when (tab) {
            InventoryFilterTab.ALL_PRODUCTS -> allProducts
            InventoryFilterTab.LOW_STOCK -> allProducts.filter { it.stockQuantity <= it.minStockAlert }
        }

        val filteredBySearch = if (query.isBlank()) {
            filteredByTab
        } else {
            filteredByTab.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.sku.contains(query, ignoreCase = true) ||
                it.category.contains(query, ignoreCase = true)
            }
        }

        InventoryUiState(
            products = filteredBySearch.sortedBy { it.name.lowercase() },
            filterTab = tab,
            searchQuery = query,
            totalItemsCount = allProducts.size,
            lowStockCount = lowStock,
            totalStockValuation = totalValuation
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), InventoryUiState())

    fun setFilterTab(tab: InventoryFilterTab) {
        _filterTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addProduct(
        name: String,
        sku: String,
        purchasePrice: Double,
        sellingPrice: Double,
        stockQuantity: Double,
        unit: String,
        category: String,
        minStockAlert: Double
    ) {
        viewModelScope.launch {
            val p = Product(
                name = name.trim(),
                sku = sku.trim(),
                purchasePrice = purchasePrice,
                sellingPrice = sellingPrice,
                stockQuantity = stockQuantity,
                unit = unit.trim().ifEmpty { "Pcs" },
                category = category.trim().ifEmpty { "General" },
                minStockAlert = if (minStockAlert > 0) minStockAlert else 5.0
            )
            repository.insertProduct(p)
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product)
        }
    }

    fun adjustStock(productId: Long, currentStock: Double, delta: Double) {
        viewModelScope.launch {
            val newStock = Math.max(0.0, currentStock + delta)
            repository.updateProductStock(productId, newStock)
        }
    }

    fun deleteProduct(productId: Long) {
        viewModelScope.launch {
            repository.deleteProduct(productId)
        }
    }

    class Factory(private val repository: LedgerRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return InventoryViewModel(repository) as T
        }
    }
}
