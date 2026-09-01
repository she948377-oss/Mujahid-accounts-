package com.businessledger.presentation.screens.reports

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
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
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.businessledger.presentation.theme.CreditGreen
import com.businessledger.presentation.theme.CreditGreenBg
import com.businessledger.presentation.theme.DebitRed
import com.businessledger.presentation.theme.DebitRedBg
import com.businessledger.presentation.theme.EmeraldDark
import com.businessledger.presentation.theme.EmeraldGreen
import com.businessledger.presentation.viewmodel.ReportType
import com.businessledger.presentation.viewmodel.ReportsViewModel
import com.businessledger.utils.DisplaySettingsManager
import com.businessledger.utils.UrduLocalization

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel,
    onPartyClick: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val settings by viewModel.displaySettings.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.fillMaxSize().testTag("reports_screen")
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Report Types Tab Bar
            val reportTabs = listOf(
                ReportType.BALANCE_SHEET to "Balance Sheet",
                ReportType.RECEIVABLE_LIST to "Debtors (Receivable)",
                ReportType.PAYABLE_LIST to "Creditors (Payable)",
                ReportType.CASH_FLOW_SUMMARY to "Cash Flow",
                ReportType.INVENTORY_VALUATION to "Inventory"
            )
            val selectedIndex = reportTabs.indexOfFirst { it.first == uiState.reportType }.coerceAtLeast(0)

            PrimaryScrollableTabRow(
                selectedTabIndex = selectedIndex,
                edgePadding = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                reportTabs.forEachIndexed { index, pair ->
                    Tab(
                        selected = selectedIndex == index,
                        onClick = { viewModel.setReportType(pair.first) },
                        text = {
                            Text(
                                text = pair.second,
                                fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        }
                    )
                }
            }

            // Period Filter (7 Days, 30 Days, 90 Days, 365 Days)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Period:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                val periods = listOf(7 to "7 Days", 30 to "30 Days", 90 to "3 Months", 365 to "1 Year")
                periods.forEach { (days, label) ->
                    FilterChip(
                        selected = uiState.selectedPeriodDays == days,
                        onClick = { viewModel.setPeriodDays(days) },
                        label = { Text(label, fontSize = 11.sp) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                when (uiState.reportType) {
                    ReportType.BALANCE_SHEET -> {
                        item {
                            BalanceSheetOverview(
                                uiState = uiState,
                                settings = settings,
                                onShare = {
                                    val text = """
                                    📊 ${settings.businessName} - Business Financial Statement
                                    ━━━━━━━━━━━━━━━━━━━━━━━━━
                                    📅 Period: Last ${uiState.selectedPeriodDays} Days
                                    
                                    📥 Total Receivable: ${settings.currencySymbol} ${uiState.totalReceivable}
                                    📤 Total Payable: ${settings.currencySymbol} ${uiState.totalPayable}
                                    📦 Stock Valuation: ${settings.currencySymbol} ${uiState.stockValuation}
                                    💵 Net Cash Flow: ${settings.currencySymbol} ${uiState.netCashFlow}
                                    ━━━━━━━━━━━━━━━━━━━━━━━━━
                                    💎 Estimated Net Business Worth: ${settings.currencySymbol} ${uiState.netWorth}
                                    
                                    Generated by Mujahid Accounts
                                    """.trimIndent()
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        putExtra(Intent.EXTRA_TEXT, text)
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(intent, "Share Balance Sheet"))
                                }
                            )
                        }
                    }

                    ReportType.RECEIVABLE_LIST -> {
                        item {
                            ReportSectionHeader(
                                title = "Debtors (To Receive)",
                                total = uiState.totalReceivable,
                                count = uiState.debtorsList.size,
                                color = CreditGreen,
                                settings = settings
                            )
                        }
                        items(uiState.debtorsList, key = { it.id }) { party ->
                            ReportPartyItem(
                                party = party,
                                settings = settings,
                                isReceivable = true,
                                onClick = { onPartyClick(party.id) }
                            )
                        }
                    }

                    ReportType.PAYABLE_LIST -> {
                        item {
                            ReportSectionHeader(
                                title = "Creditors (To Pay)",
                                total = uiState.totalPayable,
                                count = uiState.creditorsList.size,
                                color = DebitRed,
                                settings = settings
                            )
                        }
                        items(uiState.creditorsList, key = { it.id }) { party ->
                            ReportPartyItem(
                                party = party,
                                settings = settings,
                                isReceivable = false,
                                onClick = { onPartyClick(party.id) }
                            )
                        }
                    }

                    ReportType.CASH_FLOW_SUMMARY -> {
                        item {
                            CashFlowReportSection(
                                uiState = uiState,
                                settings = settings
                            )
                        }
                    }

                    ReportType.INVENTORY_VALUATION -> {
                        item {
                            InventoryReportSection(
                                uiState = uiState,
                                settings = settings
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BalanceSheetOverview(
    uiState: com.businessledger.presentation.viewmodel.ReportsUiState,
    settings: com.businessledger.data.local.entity.DisplaySettingsEntity,
    onShare: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Business Net Worth",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = DisplaySettingsManager.formatPrice(uiState.netWorth, settings),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(
                    onClick = onShare,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Breakdown Grid
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ReportMetricRow(
                    title = "Total Receivable",
                    subtitle = "${uiState.debtorsList.size} Customers with balance",
                    amount = uiState.totalReceivable,
                    color = CreditGreen,
                    settings = settings
                )

                ReportMetricRow(
                    title = "Total Inventory Stock",
                    subtitle = "${uiState.allProducts.size} Products in warehouse/shop",
                    amount = uiState.stockValuation,
                    color = Color(0xFFD97706),
                    settings = settings
                )

                ReportMetricRow(
                    title = "Net Cash Flow",
                    subtitle = "Cash In minus Cash Out",
                    amount = uiState.netCashFlow,
                    color = if (uiState.netCashFlow >= 0) CreditGreen else DebitRed,
                    settings = settings
                )

                ReportMetricRow(
                    title = "Total Payable",
                    subtitle = "${uiState.creditorsList.size} Suppliers to pay",
                    amount = -uiState.totalPayable,
                    color = DebitRed,
                    settings = settings
                )
            }
        }
    }
}

@Composable
private fun ReportMetricRow(
    title: String,
    subtitle: String,
    amount: Double,
    color: Color,
    settings: com.businessledger.data.local.entity.DisplaySettingsEntity
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                text = DisplaySettingsManager.formatPrice(amount, settings),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = color
            )
        }
    }
}

@Composable
private fun ReportSectionHeader(
    title: String,
    total: Double,
    count: Int,
    color: Color,
    settings: com.businessledger.data.local.entity.DisplaySettingsEntity
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = color)
                Text(text = "$count Parties Listed", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                text = DisplaySettingsManager.formatPrice(total, settings),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
private fun ReportPartyItem(
    party: com.businessledger.domain.model.Party,
    settings: com.businessledger.data.local.entity.DisplaySettingsEntity,
    isReceivable: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = party.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                Text(text = if (party.phone.isNotEmpty()) party.phone else "Party #${party.id}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                text = DisplaySettingsManager.formatPrice(Math.abs(party.currentBalance), settings),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = if (isReceivable) CreditGreen else DebitRed
            )
        }
    }
}

@Composable
private fun CashFlowReportSection(
    uiState: com.businessledger.presentation.viewmodel.ReportsUiState,
    settings: com.businessledger.data.local.entity.DisplaySettingsEntity
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Cash In vs Cash Out Flow", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Cash In", color = CreditGreen, style = MaterialTheme.typography.labelMedium)
                        Text(DisplaySettingsManager.formatPrice(uiState.totalCashIn, settings), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = CreditGreen)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Cash Out", color = DebitRed, style = MaterialTheme.typography.labelMedium)
                        Text(DisplaySettingsManager.formatPrice(uiState.totalCashOut, settings), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = DebitRed)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Net Cash Flow", style = MaterialTheme.typography.labelMedium)
                        Text(
                            DisplaySettingsManager.formatPrice(uiState.netCashFlow, settings),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (uiState.netCashFlow >= 0) CreditGreen else DebitRed
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InventoryReportSection(
    uiState: com.businessledger.presentation.viewmodel.ReportsUiState,
    settings: com.businessledger.data.local.entity.DisplaySettingsEntity
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Inventory Health & Valuation", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Total Stock Value", style = MaterialTheme.typography.labelSmall)
                        Text(DisplaySettingsManager.formatPrice(uiState.stockValuation, settings), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Total Products", style = MaterialTheme.typography.labelSmall)
                        Text("${uiState.allProducts.size} items", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}
