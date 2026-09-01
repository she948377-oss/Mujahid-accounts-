package com.businessledger.presentation.screens.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.businessledger.domain.model.Product
import com.businessledger.presentation.theme.CreditGreen
import com.businessledger.presentation.theme.DebitRed
import com.businessledger.presentation.theme.DebitRedBg
import com.businessledger.presentation.theme.EmeraldGreen
import com.businessledger.presentation.viewmodel.InventoryFilterTab
import com.businessledger.presentation.viewmodel.InventoryViewModel
import com.businessledger.utils.DisplaySettingsManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    viewModel: InventoryViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val settings by viewModel.displaySettings.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<Product?>(null) }
    var productToDelete by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize().testTag("inventory_screen"),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add Item (نئی آئٹم)") },
                containerColor = EmeraldGreen,
                contentColor = Color.White,
                modifier = Modifier.padding(bottom = 72.dp).testTag("add_product_fab")
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Inventory Top Summary Card
            InventorySummaryCard(
                totalCount = uiState.totalItemsCount,
                lowStockCount = uiState.lowStockCount,
                valuation = uiState.totalStockValuation,
                settings = settings
            )

            // Search Bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search product name, code or category...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("inventory_search_input"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Tabs: All Products vs Low Stock Warning
            val tabs = listOf(
                InventoryFilterTab.ALL_PRODUCTS to "All Stock (${uiState.totalItemsCount})",
                InventoryFilterTab.LOW_STOCK to "Low Stock Alert (${uiState.lowStockCount})"
            )
            val selectedTabIndex = if (uiState.filterTab == InventoryFilterTab.ALL_PRODUCTS) 0 else 1

            PrimaryTabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, pair ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { viewModel.setFilterTab(pair.first) },
                        text = {
                            Text(
                                text = pair.second,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (index == 1 && uiState.lowStockCount > 0) DebitRed else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    )
                }
            }

            // Products List
            if (uiState.products.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Inventory,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No stock items found",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tap '+ Add Item' to add products with cost and selling price.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.products, key = { it.id }) { product ->
                        ProductItemCard(
                            product = product,
                            settings = settings,
                            onStockIncrease = { viewModel.adjustStock(product.id, product.stockQuantity, 1.0) },
                            onStockDecrease = { viewModel.adjustStock(product.id, product.stockQuantity, -1.0) },
                            onEdit = { editingProduct = product },
                            onDelete = { productToDelete = product.id }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddEditProductDialog(
            product = null,
            currencySymbol = settings.currencySymbol,
            onDismiss = { showAddDialog = false },
            onConfirm = { name, sku, purchase, selling, stock, unit, category, minAlert ->
                viewModel.addProduct(name, sku, purchase, selling, stock, unit, category, minAlert)
                showAddDialog = false
            }
        )
    }

    if (editingProduct != null) {
        AddEditProductDialog(
            product = editingProduct,
            currencySymbol = settings.currencySymbol,
            onDismiss = { editingProduct = null },
            onConfirm = { name, sku, purchase, selling, stock, unit, category, minAlert ->
                val p = editingProduct
                if (p != null) {
                    viewModel.updateProduct(
                        p.copy(
                            name = name,
                            sku = sku,
                            purchasePrice = purchase,
                            sellingPrice = selling,
                            stockQuantity = stock,
                            unit = unit,
                            category = category,
                            minStockAlert = minAlert,
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                }
                editingProduct = null
            }
        )
    }

    if (productToDelete != null) {
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            title = { Text("Delete Stock Item") },
            text = { Text("Are you sure you want to delete this product from your inventory?") },
            confirmButton = {
                Button(
                    onClick = {
                        val id = productToDelete
                        if (id != null) viewModel.deleteProduct(id)
                        productToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DebitRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { productToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun InventorySummaryCard(
    totalCount: Int,
    lowStockCount: Int,
    valuation: Double,
    settings: com.businessledger.data.local.entity.DisplaySettingsEntity
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Total Stock Valuation (کل مالیت)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = DisplaySettingsManager.formatPrice(valuation, settings),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "$totalCount total active products",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (lowStockCount > 0) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(DebitRedBg)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = DebitRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$lowStockCount Low Stock",
                            style = MaterialTheme.typography.labelSmall,
                            color = DebitRed,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductItemCard(
    product: Product,
    settings: com.businessledger.data.local.entity.DisplaySettingsEntity,
    onStockIncrease: () -> Unit,
    onStockDecrease: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isLowStock = product.stockQuantity <= product.minStockAlert
    val margin = product.sellingPrice - product.purchasePrice

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("product_item_${product.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (isLowStock) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(DebitRedBg)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Low Stock",
                                    fontSize = 10.sp,
                                    color = DebitRed,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Text(
                        text = "${product.category} • SKU: ${product.sku.ifEmpty { "N/A" }}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DebitRed, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pricing & Stock Counter Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Sale: ${settings.currencySymbol} ${product.sellingPrice} | Cost: ${settings.currencySymbol} ${product.purchasePrice}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Margin: ${settings.currencySymbol} $margin per ${product.unit}",
                        style = MaterialTheme.typography.labelSmall,
                        color = CreditGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Quick Stock Adjuster (+ / -)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    IconButton(
                        onClick = onStockDecrease,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(14.dp))
                    }

                    Text(
                        text = "${product.stockQuantity.toInt()} ${product.unit}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isLowStock) DebitRed else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )

                    IconButton(
                        onClick = onStockIncrease,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(14.dp), tint = CreditGreen)
                    }
                }
            }
        }
    }
}

@Composable
private fun AddEditProductDialog(
    product: Product?,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onConfirm: (
        name: String,
        sku: String,
        purchasePrice: Double,
        sellingPrice: Double,
        stockQuantity: Double,
        unit: String,
        category: String,
        minStockAlert: Double
    ) -> Unit
) {
    var name by remember { mutableStateOf(product?.name ?: "") }
    var sku by remember { mutableStateOf(product?.sku ?: "") }
    var purchasePriceStr by remember { mutableStateOf(product?.purchasePrice?.toString() ?: "") }
    var sellingPriceStr by remember { mutableStateOf(product?.sellingPrice?.toString() ?: "") }
    var stockQuantityStr by remember { mutableStateOf(product?.stockQuantity?.toString() ?: "1") }
    var unit by remember { mutableStateOf(product?.unit ?: "Pcs") }
    var category by remember { mutableStateOf(product?.category ?: "General") }
    var minStockAlertStr by remember { mutableStateOf(product?.minStockAlert?.toString() ?: "5") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (product == null) "Add Product (نئی آئٹم درج کریں)" else "Edit Product",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Product Name (پراڈکٹ کا نام) *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("product_name_input")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = purchasePriceStr,
                        onValueChange = { purchasePriceStr = it },
                        label = { Text("Cost ($currencySymbol) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("product_purchase_input")
                    )

                    OutlinedTextField(
                        value = sellingPriceStr,
                        onValueChange = { sellingPriceStr = it },
                        label = { Text("Sale ($currencySymbol) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("product_selling_input")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = stockQuantityStr,
                        onValueChange = { stockQuantityStr = it },
                        label = { Text("Initial Stock") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("product_stock_input")
                    )

                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Unit (Pcs/Kg/Box)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = minStockAlertStr,
                        onValueChange = { minStockAlertStr = it },
                        label = { Text("Low Stock Alert") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val cost = purchasePriceStr.toDoubleOrNull() ?: 0.0
                        val sale = sellingPriceStr.toDoubleOrNull() ?: 0.0
                        val stock = stockQuantityStr.toDoubleOrNull() ?: 0.0
                        val minAlert = minStockAlertStr.toDoubleOrNull() ?: 5.0
                        onConfirm(name, sku, cost, sale, stock, unit, category, minAlert)
                    }
                },
                enabled = name.isNotBlank(),
                modifier = Modifier.testTag("save_product_button")
            ) {
                Text("Save Item")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}
