package com.businessledger.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.businessledger.BusinessLedgerApp
import com.businessledger.presentation.navigation.MainAppNavHost
import com.businessledger.presentation.theme.BusinessLedgerTheme
import com.businessledger.presentation.viewmodel.CashbookViewModel
import com.businessledger.presentation.viewmodel.DashboardViewModel
import com.businessledger.presentation.viewmodel.InventoryViewModel
import com.businessledger.presentation.viewmodel.PartiesViewModel
import com.businessledger.presentation.viewmodel.PartyDetailViewModel
import com.businessledger.presentation.viewmodel.ReportsViewModel
import com.businessledger.presentation.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {

    private val appModule by lazy { (application as BusinessLedgerApp).appModule }

    private val dashboardViewModel by viewModels<DashboardViewModel> {
        viewModelFactory { DashboardViewModel(appModule.repository, appModule.getDashboardSummaryUseCase) }
    }

    private val partiesViewModel by viewModels<PartiesViewModel> {
        viewModelFactory { PartiesViewModel(appModule.repository) }
    }

    private val partyDetailViewModel by viewModels<PartyDetailViewModel> {
        viewModelFactory { PartyDetailViewModel(appModule.repository, appModule.getPartyLedgerUseCase) }
    }

    private val cashbookViewModel by viewModels<CashbookViewModel> {
        viewModelFactory { CashbookViewModel(appModule.repository) }
    }

    private val inventoryViewModel by viewModels<InventoryViewModel> {
        viewModelFactory { InventoryViewModel(appModule.repository) }
    }

    private val reportsViewModel by viewModels<ReportsViewModel> {
        viewModelFactory { ReportsViewModel(appModule.repository) }
    }

    private val settingsViewModel by viewModels<SettingsViewModel> {
        viewModelFactory { SettingsViewModel(appModule.repository) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BusinessLedgerTheme {
                val navController = rememberNavController()
                MainAppNavHost(
                    navController = navController,
                    dashboardViewModel = dashboardViewModel,
                    partiesViewModel = partiesViewModel,
                    partyDetailViewModel = partyDetailViewModel,
                    cashbookViewModel = cashbookViewModel,
                    inventoryViewModel = inventoryViewModel,
                    reportsViewModel = reportsViewModel,
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified T : ViewModel> viewModelFactory(crossinline creator: () -> T): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            override fun <VM : ViewModel> create(modelClass: Class<VM>): VM {
                return creator() as VM
            }
        }
    }
}
