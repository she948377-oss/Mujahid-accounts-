package com.businessledger.presentation.screens.cashbook

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import com.businessledger.domain.model.CashEntry
import com.businessledger.domain.model.CashEntryType
import com.businessledger.presentation.theme.CreditGreen
import com.businessledger.presentation.theme.CreditGreenBg
import com.businessledger.presentation.theme.DebitRed
import com.businessledger.presentation.theme.DebitRedBg
import com.businessledger.presentation.viewmodel.CashbookFilterPeriod
import com.businessledger.presentation.viewmodel.CashbookViewModel
import com.businessledger.utils.DisplaySettingsManager
import com.businessledger.utils.UrduLocalization

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashbookScreen(
    viewModel: CashbookViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val settings by viewModel.displaySettings.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var activeAddType by remember { mutableStateOf(CashEntryType.CASH_IN) }
    var entryToDelete by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize().testTag("cashbook_screen"),
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            activeAddType = CashEntryType.CASH_OUT
                            showAddDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DebitRed,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("cash_out_bottom_button")
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("CASH OUT (خرچ)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("ادائیگی / اخراجات", fontSize = 10.sp)
                        }
                    }

                    Button(
                        onClick = {
                            activeAddType = CashEntryType.CASH_IN
                            showAddDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CreditGreen,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("cash_in_bottom_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("CASH IN (آمد)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("وصولی / کیش سیل", fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Period Tabs (Today, This Week, This Month, All)
            val periodTabs = listOf(
                CashbookFilterPeriod.TODAY to "Today (آج)",
                CashbookFilterPeriod.THIS_WEEK to "Week (ہفتہ)",
                CashbookFilterPeriod.THIS_MONTH to "Month (مہینہ)",
                CashbookFilterPeriod.ALL to "All (تمام)"
            )
            val selectedTabIndex = when (uiState.period) {
                CashbookFilterPeriod.TODAY -> 0
                CashbookFilterPeriod.THIS_WEEK -> 1
                CashbookFilterPeriod.THIS_MONTH -> 2
                CashbookFilterPeriod.ALL -> 3
            }

            PrimaryTabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth()
            ) {
                periodTabs.forEachIndexed { index, pair ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { viewModel.setPeriod(pair.first) },
                        text = {
                            Text(
                                text = pair.second,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        }
                    )
                }
            }

            // Cash in Hand Summary Card
            CashbookSummaryCard(
                cashIn = uiState.totalCashIn,
                cashOut = uiState.totalCashOut,
                netBalance = uiState.netCashBalance,
                settings = settings
            )

            // Category/Type Filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.typeFilter == null,
                    onClick = { viewModel.setTypeFilter(null) },
                    label = { Text("All Entries (${uiState.entries.size})") }
                )
                FilterChip(
                    selected = uiState.typeFilter == CashEntryType.CASH_IN,
                    onClick = { viewModel.setTypeFilter(CashEntryType.CASH_IN) },
                    label = { Text("Cash In (آمد)") }
                )
                FilterChip(
                    selected = uiState.typeFilter == CashEntryType.CASH_OUT,
                    onClick = { viewModel.setTypeFilter(CashEntryType.CASH_OUT) },
                    label = { Text("Cash Out (خرچ)") }
                )
            }

            // Entries List
            if (uiState.entries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No cashbook records for this period",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tap 'Cash In' or 'Cash Out' below to log counter sales and daily expenses.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.entries, key = { it.id }) { entry ->
                        CashbookEntryItem(
                            entry = entry,
                            currencySymbol = settings.currencySymbol,
                            onDelete = { entryToDelete = entry.id }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddCashbookEntryDialog(
            initialType = activeAddType,
            currencySymbol = settings.currencySymbol,
            onDismiss = { showAddDialog = false },
            onConfirm = { type, amount, category, description, paymentMode ->
                viewModel.addEntry(type, amount, category, description, paymentMode)
                showAddDialog = false
            }
        )
    }

    if (entryToDelete != null) {
        AlertDialog(
            onDismissRequest = { entryToDelete = null },
            title = { Text("Delete Cash Entry") },
            text = { Text("Are you sure you want to delete this cashbook record?") },
            confirmButton = {
                Button(
                    onClick = {
                        val id = entryToDelete
                        if (id != null) viewModel.deleteEntry(id)
                        entryToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DebitRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { entryToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun CashbookSummaryCard(
    cashIn: Double,
    cashOut: Double,
    netBalance: Double,
    settings: com.businessledger.data.local.entity.DisplaySettingsEntity
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Net Cash Balance (کیش ان ہینڈ)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    val netColor = if (netBalance >= 0) CreditGreen else DebitRed
                    Text(
                        text = DisplaySettingsManager.formatPrice(netBalance, settings),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = netColor
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (netBalance >= 0) CreditGreenBg else DebitRedBg)
                        .padding(10.dp)
                ) {
                    Icon(
                        imageVector = if (netBalance >= 0) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                        contentDescription = null,
                        tint = if (netBalance >= 0) CreditGreen else DebitRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Total Cash In (آمد)", style = MaterialTheme.typography.labelSmall, color = CreditGreen)
                    Text(
                        text = DisplaySettingsManager.formatPrice(cashIn, settings),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = CreditGreen
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(28.dp)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Total Cash Out (خرچ)", style = MaterialTheme.typography.labelSmall, color = DebitRed)
                    Text(
                        text = DisplaySettingsManager.formatPrice(cashOut, settings),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = DebitRed
                    )
                }
            }
        }
    }
}

@Composable
private fun CashbookEntryItem(
    entry: CashEntry,
    currencySymbol: String,
    onDelete: () -> Unit
) {
    val isIn = entry.type == CashEntryType.CASH_IN
    val amountColor = if (isIn) CreditGreen else DebitRed

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("cash_entry_${entry.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (isIn) CreditGreenBg else DebitRedBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isIn) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                        contentDescription = null,
                        tint = amountColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = entry.category,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = entry.paymentMode,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp
                            )
                        }
                    }

                    if (entry.description.isNotEmpty()) {
                        Text(
                            text = entry.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = UrduLocalization.formatDate(entry.date),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${if (isIn) "+" else "-"}$currencySymbol ${entry.amount}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = amountColor
                    )
                    Text(
                        text = if (isIn) "Received (آمد)" else "Paid (خرچ)",
                        style = MaterialTheme.typography.labelSmall,
                        color = amountColor
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddCashbookEntryDialog(
    initialType: CashEntryType,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onConfirm: (
        type: CashEntryType,
        amount: Double,
        category: String,
        description: String,
        paymentMode: String
    ) -> Unit
) {
    var type by remember { mutableStateOf(initialType) }
    var amountStr by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(if (initialType == CashEntryType.CASH_IN) "Counter Sales" else "Shop Expenses") }
    var description by remember { mutableStateOf("") }
    var paymentMode by remember { mutableStateOf("Cash") }

    val categories = if (type == CashEntryType.CASH_IN) {
        listOf("Counter Sales", "Direct Payment", "Loan Return", "Commission", "Other Income")
    } else {
        listOf("Shop Expenses", "Tea / Food", "Electricity Bill", "Shop Rent", "Staff Salary", "Transport", "Repair", "Personal")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (type == CashEntryType.CASH_IN) "Cash In (آمد کیش درج کریں)" else "Cash Out (خرچ کیش درج کریں)",
                color = if (type == CashEntryType.CASH_IN) CreditGreen else DebitRed,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = type == CashEntryType.CASH_IN,
                        onClick = {
                            type = CashEntryType.CASH_IN
                            category = "Counter Sales"
                        },
                        label = { Text("Cash In (آمد)") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = type == CashEntryType.CASH_OUT,
                        onClick = {
                            type = CashEntryType.CASH_OUT
                            category = "Shop Expenses"
                        },
                        label = { Text("Cash Out (خرچ)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Amount ($currencySymbol) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("cashbook_amount_input")
                )

                Text("Category (کیٹیگری):", style = MaterialTheme.typography.labelMedium)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description / Remarks (تفصیل)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("cashbook_desc_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountStr.toDoubleOrNull() ?: 0.0
                    if (amt > 0) {
                        onConfirm(type, amt, category, description, paymentMode)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (type == CashEntryType.CASH_IN) CreditGreen else DebitRed
                ),
                enabled = (amountStr.toDoubleOrNull() ?: 0.0) > 0,
                modifier = Modifier.testTag("save_cashbook_entry_button")
            ) {
                Text("Save")
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
