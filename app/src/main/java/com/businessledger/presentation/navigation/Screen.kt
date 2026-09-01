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

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    data object Parties : Screen("parties", "Parties", Icons.Default.People)
    data object Cashbook : Screen("cashbook", "Cashbook", Icons.Default.Payments)
    data object Inventory : Screen("inventory", "Inventory", Icons.Default.Inventory)
    data object Reports : Screen("reports", "Reports", Icons.Default.Assessment)
    data object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    data object PartyDetail : Screen("party_detail/{partyId}", "Party Ledger", Icons.Default.MenuBook) {
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
