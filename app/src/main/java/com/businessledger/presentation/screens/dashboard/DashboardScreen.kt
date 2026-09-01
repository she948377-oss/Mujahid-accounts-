package com.businessledger.presentation.screens.dashboard

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.businessledger.domain.model.CashEntryType
import com.businessledger.domain.model.PartyType
import com.businessledger.domain.model.Transaction
import com.businessledger.domain.model.TransactionType
import com.businessledger.presentation.theme.CreditGreen
import com.businessledger.presentation.theme.CreditGreenBg
import com.businessledger.presentation.theme.DebitRed
import com.businessledger.presentation.theme.DebitRedBg
import com.businessledger.presentation.theme.EmeraldDark
import com.businessledger.presentation.theme.EmeraldGreen
import com.businessledger.presentation.theme.FintechDarkBorder
import com.businessledger.presentation.theme.FintechDarkCard
import com.businessledger.presentation.theme.FintechDarkCardElevated
import com.businessledger.presentation.theme.FintechEmerald
import com.businessledger.presentation.theme.FintechEmeraldDark
import com.businessledger.presentation.theme.FintechEmeraldGlow
import com.businessledger.presentation.viewmodel.DashboardViewModel
import com.businessledger.utils.DisplaySettingsManager
import com.businessledger.utils.UrduLocalization

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToParties: () -> Unit,
    onNavigateToCashbook: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToReports: () -> Unit,
    onPartyClick: (Long) -> Unit = {},
    onOpenSettings: () -> Unit = {}
) {
    val summary by viewModel.summaryState.collectAsStateWithLifecycle()
    val recentTxs by viewModel.recentTransactions.collectAsStateWithLifecycle()
    val settings by viewModel.displaySettings.collectAsStateWithLifecycle()

    var showAddPartyDialog by remember { mutableStateOf(false) }
    var showAddCashDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // App Top Bar / Hero Header
        item {
            HeaderSection(
                businessName = settings.businessName,
                businessPhone = settings.businessPhone,
                onOpenSettings = onOpenSettings
            )
        }

        // Financial Ledger Overview Cards (Net Balance, Receivable, Payable)
        item {
            LedgerCardsSection(
                summary = summary,
                settings = settings,
                onNavigateToParties = onNavigateToParties
            )
        }

        // Today's Cash Flow Strip
        item {
            CashFlowStrip(
                summary = summary,
                settings = settings,
                onNavigateToCashbook = onNavigateToCashbook,
                onAddCashClick = { showAddCashDialog = true }
            )
        }

        // Quick Actions Row
        item {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            QuickActionsSection(
                onAddParty = { showAddPartyDialog = true },
                onAddCash = { showAddCashDialog = true },
                onInventory = onNavigateToInventory,
                onReports = onNavigateToReports
            )
        }

        // Statistics Chips
        item {
            BusinessStatsSection(
                summary = summary,
                settings = settings,
                onNavigateToInventory = onNavigateToInventory
            )
        }

        // Recent Ledger Transactions
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Recent Transactions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Latest ledger entries across all khatas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                TextButton(
                    onClick = onNavigateToParties,
                    modifier = Modifier.testTag("view_all_transactions_button")
                ) {
                    Text("View All")
                }
            }
        }

        if (recentTxs.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No entries recorded yet", fontWeight = FontWeight.SemiBold)
                        Text(
                            "Add your first customer or supplier to start your ledger.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { showAddPartyDialog = true },
                            modifier = Modifier.testTag("first_party_button")
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add First Party")
                        }
                    }
                }
            }
        } else {
            items(recentTxs.take(5)) { tx ->
                RecentTransactionItem(
                    tx = tx,
                    currencySymbol = settings.currencySymbol
                )
            }
        }
    }

    if (showAddPartyDialog) {
        QuickAddPartyDialog(
            onDismiss = { showAddPartyDialog = false },
            onConfirm = { name, phone, type, bal ->
                viewModel.quickAddParty(name, phone, type, bal)
                showAddPartyDialog = false
            }
        )
    }

    if (showAddCashDialog) {
        QuickAddCashDialog(
            onDismiss = { showAddCashDialog = false },
            onConfirm = { type, amount, category, desc ->
                viewModel.quickAddCashEntry(type, amount, category, desc)
                showAddCashDialog = false
            }
        )
    }
}

@Composable
private fun HeaderSection(
    businessName: String,
    businessPhone: String,
    onOpenSettings: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF0A0F1D)
                    )
                )
            )
            .padding(horizontal = 20.dp, vertical = 22.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    FintechEmerald,
                                    FintechEmeraldDark
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Business,
                        contentDescription = "Business Logo",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = businessName.ifEmpty { "Mujahid Accounts" },
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (businessPhone.isNotEmpty()) businessPhone else "Digital Khata & Ledger",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFF1E293B))
                    .testTag("dashboard_settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = FintechEmerald
                )
            }
        }
    }
}

@Composable
private fun LedgerCardsSection(
    summary: com.businessledger.domain.model.DashboardSummary,
    settings: com.businessledger.data.local.entity.DisplaySettingsEntity,
    onNavigateToParties: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Main Net Balance Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToParties() }
                .testTag("net_balance_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, FintechDarkBorder)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TOTAL NET BALANCE",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        val netColor = if (summary.netBalance >= 0) CreditGreen else DebitRed
                        Text(
                            text = DisplaySettingsManager.formatPrice(summary.netBalance, settings),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = netColor
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                if (summary.netBalance >= 0) CreditGreenBg else DebitRedBg
                            )
                            .padding(12.dp)
                    ) {
                        Icon(
                            imageVector = if (summary.netBalance >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                            contentDescription = null,
                            tint = if (summary.netBalance >= 0) CreditGreen else DebitRed,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Split Receivable and Payable Bars
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Lena Hai (Receivable)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(CreditGreenBg)
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDownward,
                                    contentDescription = null,
                                    tint = CreditGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "To Receive (Lena Hai)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CreditGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = DisplaySettingsManager.formatPrice(summary.totalReceivable, settings),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = CreditGreen
                            )
                        }
                    }

                    // Dena Hai (Payable)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(DebitRedBg)
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ArrowUpward,
                                    contentDescription = null,
                                    tint = DebitRed,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "To Pay (Dena Hai)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = DebitRed,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = DisplaySettingsManager.formatPrice(summary.totalPayable, settings),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = DebitRed
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CashFlowStrip(
    summary: com.businessledger.domain.model.DashboardSummary,
    settings: com.businessledger.data.local.entity.DisplaySettingsEntity,
    onNavigateToCashbook: () -> Unit,
    onAddCashClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onNavigateToCashbook() }
            .testTag("today_cashflow_card"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, FintechDarkBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Today's Cash Flow",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Open Cashbook →",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Cash In",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = DisplaySettingsManager.formatPrice(summary.todayCashIn, settings),
                        style = MaterialTheme.typography.titleSmall,
                        color = CreditGreen,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column {
                    Text(
                        text = "Cash Out",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = DisplaySettingsManager.formatPrice(summary.todayCashOut, settings),
                        style = MaterialTheme.typography.titleSmall,
                        color = DebitRed,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Net Cash",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    val netTodayColor = if (summary.todayNetCash >= 0) CreditGreen else DebitRed
                    Text(
                        text = DisplaySettingsManager.formatPrice(summary.todayNetCash, settings),
                        style = MaterialTheme.typography.titleSmall,
                        color = netTodayColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionsSection(
    onAddParty: () -> Unit,
    onAddCash: () -> Unit,
    onInventory: () -> Unit,
    onReports: () -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            QuickActionButton(
                icon = Icons.Default.PersonAdd,
                title = "New Khata",
                subtitle = "Customer/Supplier",
                color = EmeraldGreen,
                onClick = onAddParty,
                testTag = "quick_action_add_party"
            )
        }
        item {
            QuickActionButton(
                icon = Icons.Default.Add,
                title = "Cash In/Out",
                subtitle = "Record Entry",
                color = Color(0xFF0284C7),
                onClick = onAddCash,
                testTag = "quick_action_add_cash"
            )
        }
        item {
            QuickActionButton(
                icon = Icons.Default.Inventory,
                title = "Inventory",
                subtitle = "Products & Stock",
                color = Color(0xFFD97706),
                onClick = onInventory,
                testTag = "quick_action_inventory"
            )
        }
        item {
            QuickActionButton(
                icon = Icons.Default.TrendingUp,
                title = "Reports",
                subtitle = "Statements",
                color = Color(0xFF7C3AED),
                onClick = onReports,
                testTag = "quick_action_reports"
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = color.copy(alpha = 0.12f),
        modifier = Modifier.testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun BusinessStatsSection(
    summary: com.businessledger.domain.model.DashboardSummary,
    settings: com.businessledger.data.local.entity.DisplaySettingsEntity,
    onNavigateToInventory: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Parties",
            value = "${summary.totalCustomersCount + summary.totalSuppliersCount}",
            subtitle = "${summary.totalCustomersCount} Cus • ${summary.totalSuppliersCount} Sup",
            icon = Icons.Default.Group,
            iconColor = EmeraldGreen
        )

        StatCard(
            modifier = Modifier.weight(1f),
            title = "Stock Value",
            value = DisplaySettingsManager.formatPrice(summary.totalStockValuation, settings),
            subtitle = "${summary.totalProductsCount} Products",
            icon = Icons.Default.Inventory,
            iconColor = Color(0xFFD97706),
            onClick = onNavigateToInventory
        )

        if (summary.lowStockCount > 0) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Low Stock",
                value = "${summary.lowStockCount}",
                subtitle = "Re-order needed",
                icon = Icons.Default.NotificationsActive,
                iconColor = DebitRed,
                onClick = onNavigateToInventory
            )
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier.then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, FintechDarkBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun RecentTransactionItem(
    tx: Transaction,
    currencySymbol: String
) {
    val isGave = tx.type == TransactionType.GAVE
    val amountColor = if (isGave) DebitRed else CreditGreen
    val typeLabel = if (isGave) "You Gave" else "You Got"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, FintechDarkBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isGave) DebitRedBg else CreditGreenBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isGave) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = amountColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = tx.partyName.ifEmpty { "Party Transaction" },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${UrduLocalization.formatDate(tx.date)} • ${tx.description.ifEmpty { typeLabel }}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (isGave) "-" else "+"}$currencySymbol ${tx.amount}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = amountColor
                )
                Text(
                    text = typeLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun QuickAddPartyDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, phone: String, type: PartyType, openingBal: Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var partyType by remember { mutableStateOf(PartyType.CUSTOMER) }
    var openingBalanceStr by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add New Party",
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
                        selected = partyType == PartyType.CUSTOMER,
                        onClick = { partyType = PartyType.CUSTOMER },
                        label = { Text("Customer") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = partyType == PartyType.SUPPLIER,
                        onClick = { partyType = PartyType.SUPPLIER },
                        label = { Text("Supplier") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Party Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("quick_party_name_input")
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("quick_party_phone_input")
                )

                OutlinedTextField(
                    value = openingBalanceStr,
                    onValueChange = { openingBalanceStr = it },
                    label = { Text("Opening Balance") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("quick_party_balance_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val bal = openingBalanceStr.toDoubleOrNull() ?: 0.0
                        onConfirm(name, phone, partyType, bal)
                    }
                },
                enabled = name.isNotBlank(),
                modifier = Modifier.testTag("quick_party_save_button")
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

@Composable
private fun QuickAddCashDialog(
    onDismiss: () -> Unit,
    onConfirm: (type: CashEntryType, amount: Double, category: String, desc: String) -> Unit
) {
    var type by remember { mutableStateOf(CashEntryType.CASH_IN) }
    var amountStr by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Counter Sales") }
    var description by remember { mutableStateOf("") }

    val commonCategories = if (type == CashEntryType.CASH_IN) {
        listOf("Counter Sales", "Direct Payment", "Investment", "Other Income")
    } else {
        listOf("Shop Expenses", "Tea/Food", "Bill & Rent", "Staff Salary", "Other Expense")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Cash Entry",
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
                        label = { Text("Cash In") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = type == CashEntryType.CASH_OUT,
                        onClick = {
                            type = CashEntryType.CASH_OUT
                            category = "Shop Expenses"
                        },
                        label = { Text("Cash Out") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("quick_cash_amount_input")
                )

                Text("Category:", style = MaterialTheme.typography.labelMedium)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(commonCategories) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat) }
                        )
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description / Remarks") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountStr.toDoubleOrNull() ?: 0.0
                    if (amt > 0) {
                        onConfirm(type, amt, category, description)
                    }
                },
                enabled = (amountStr.toDoubleOrNull() ?: 0.0) > 0,
                modifier = Modifier.testTag("quick_cash_save_button")
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
