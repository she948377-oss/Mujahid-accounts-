package com.businessledger.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.businessledger.presentation.components.DisplaySettingsDialog
import com.businessledger.presentation.screens.cashbook.CashbookScreen
import com.businessledger.presentation.screens.dashboard.DashboardScreen
import com.businessledger.presentation.screens.inventory.InventoryScreen
import com.businessledger.presentation.screens.parties.PartiesScreen
import com.businessledger.presentation.screens.parties.PartyDetailScreen
import com.businessledger.presentation.screens.reports.ReportsScreen
import com.businessledger.presentation.screens.settings.SettingsScreen
import com.businessledger.presentation.theme.EmeraldGreen
import com.businessledger.presentation.viewmodel.CashbookViewModel
import com.businessledger.presentation.viewmodel.DashboardViewModel
import com.businessledger.presentation.viewmodel.InventoryViewModel
import com.businessledger.presentation.viewmodel.PartiesViewModel
import com.businessledger.presentation.viewmodel.PartyDetailViewModel
import com.businessledger.presentation.viewmodel.ReportsViewModel
import com.businessledger.presentation.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppNavHost(
    navController: NavHostController,
    dashboardViewModel: DashboardViewModel,
    partiesViewModel: PartiesViewModel,
    partyDetailViewModel: PartyDetailViewModel,
    cashbookViewModel: CashbookViewModel,
    inventoryViewModel: InventoryViewModel,
    reportsViewModel: ReportsViewModel,
    settingsViewModel: SettingsViewModel
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isPartyDetailScreen = currentRoute?.startsWith("party_detail") == true

    val settings by settingsViewModel.settings.collectAsStateWithLifecycle()
    var showDisplayQuickSettings by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            if (!isPartyDetailScreen) {
                CenterAlignedTopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("M", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = settings.businessName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { showDisplayQuickSettings = true },
                            modifier = Modifier.testTag("quick_display_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Display Settings",
                                tint = EmeraldGreen
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        },
        bottomBar = {
            if (!isPartyDetailScreen) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    bottomNavItems.forEach { screen ->
                        val selected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(Screen.Dashboard.route) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.title
                                )
                            },
                            label = {
                                Text(
                                    text = screen.urduTitle,
                                    fontSize = 11.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = EmeraldGreen,
                                selectedTextColor = EmeraldGreen,
                                indicatorColor = EmeraldGreen.copy(alpha = 0.12f)
                            ),
                            modifier = Modifier.testTag("nav_${screen.route}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = dashboardViewModel,
                    onNavigateToParties = { navController.navigate(Screen.Parties.route) },
                    onNavigateToCashbook = { navController.navigate(Screen.Cashbook.route) },
                    onNavigateToInventory = { navController.navigate(Screen.Inventory.route) },
                    onNavigateToReports = { navController.navigate(Screen.Reports.route) },
                    onPartyClick = { partyId ->
                        navController.navigate(Screen.PartyDetail.createRoute(partyId))
                    },
                    onOpenSettings = { navController.navigate(Screen.Settings.route) }
                )
            }

            composable(Screen.Parties.route) {
                PartiesScreen(
                    viewModel = partiesViewModel,
                    onPartyClick = { partyId ->
                        navController.navigate(Screen.PartyDetail.createRoute(partyId))
                    }
                )
            }

            composable(
                route = Screen.PartyDetail.route,
                arguments = listOf(navArgument("partyId") { type = NavType.LongType })
            ) { backStackEntry ->
                val partyId = backStackEntry.arguments?.getLong("partyId") ?: 0L
                PartyDetailScreen(
                    partyId = partyId,
                    viewModel = partyDetailViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Cashbook.route) {
                CashbookScreen(viewModel = cashbookViewModel)
            }

            composable(Screen.Inventory.route) {
                InventoryScreen(viewModel = inventoryViewModel)
            }

            composable(Screen.Reports.route) {
                ReportsScreen(
                    viewModel = reportsViewModel,
                    onPartyClick = { partyId ->
                        navController.navigate(Screen.PartyDetail.createRoute(partyId))
                    }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(viewModel = settingsViewModel)
            }
        }
    }

    if (showDisplayQuickSettings) {
        DisplaySettingsDialog(
            currentSettings = settings,
            onDismiss = { showDisplayQuickSettings = false },
            onSave = { name, phone, address, curr, lang, showBal, compact, dark ->
                settingsViewModel.updateFullSettings(
                    settings.copy(
                        businessName = name,
                        businessPhone = phone,
                        businessAddress = address,
                        currencySymbol = curr,
                        language = lang,
                        showBalanceInHeader = showBal,
                        compactView = compact,
                        isDarkMode = dark
                    )
                )
                showDisplayQuickSettings = false
            }
        )
    }
}
