package com.businessledger.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val urduTitle: String, val icon: ImageVector) {
    data object Dashboard : Screen("dashboard", "Dashboard", "ڈیش بورڈ", Icons.Default.Dashboard)
    data object Parties : Screen("parties", "Khata Parties", "کھاتہ", Icons.Default.People)
    data object Cashbook : Screen("cashbook", "Cashbook", "روکڑ", Icons.Default.Payments)
    data object Inventory : Screen("inventory", "Stock", "اسٹاک", Icons.Default.Inventory)
    data object Reports : Screen("reports", "Reports", "رپورٹس", Icons.Default.Assessment)
    data object Settings : Screen("settings", "Settings", "سیٹنگز", Icons.Default.Settings)
    data object PartyDetail : Screen("party_detail/{partyId}", "Party Ledger", "کھاتہ تفصیل", Icons.Default.MenuBook) {
        fun createRoute(partyId: Long) = "party_detail/$partyId"
    }
}

val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.Parties,
    Screen.Cashbook,
    Screen.Inventory,
    Screen.Reports,
    Screen.Settings
)
