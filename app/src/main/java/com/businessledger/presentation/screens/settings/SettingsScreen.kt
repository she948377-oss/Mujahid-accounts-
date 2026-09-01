package com.businessledger.presentation.screens.settings

import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.businessledger.presentation.theme.CreditGreen
import com.businessledger.presentation.theme.DebitRed
import com.businessledger.presentation.theme.EmeraldGreen
import com.businessledger.presentation.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showClearDataConfirm by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize().testTag("settings_screen")
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Business Profile Header Card
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth().testTag("business_profile_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Store, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(28.dp))
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = settings.businessName,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (settings.businessPhone.isNotEmpty()) settings.businessPhone else "No phone added",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (settings.businessAddress.isNotEmpty()) {
                                    Text(
                                        text = settings.businessAddress,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { showEditProfileDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Edit Business Profile")
                        }
                    }
                }
            }

            // Display & Localization Section
            item {
                Text(
                    text = "Localization & Display",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        SettingsRow(
                            icon = Icons.Default.MonetizationOn,
                            title = "Currency",
                            subtitle = "${settings.currencySymbol} (${settings.currencyCode})",
                            onClick = { showCurrencyDialog = true }
                        )

                        SettingsRow(
                            icon = Icons.Default.Language,
                            title = "App Language",
                            subtitle = when (settings.languageCode) {
                                "ur" -> "Urdu"
                                "roman" -> "Roman Urdu"
                                else -> "English (Default)"
                            },
                            onClick = { showLanguageDialog = true }
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Show Decimals (.00)", fontWeight = FontWeight.SemiBold)
                                Text("Show fractional currency amounts", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = settings.showDecimals,
                                onCheckedChange = { viewModel.updateShowDecimals(it) }
                            )
                        }
                    }
                }
            }

            // Quick Data Tools Section
            item {
                Text(
                    text = "Data Management",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CleaningServices, contentDescription = null, tint = DebitRed)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Clear Ledger & Reset Data", fontWeight = FontWeight.Bold)
                                Text("Permanently delete all ledger entries, customer/supplier records, cashbook transactions, and inventory items.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Button(
                            onClick = { showClearDataConfirm = true },
                            colors = ButtonDefaults.buttonColors(containerColor = DebitRed.copy(alpha = 0.9f)),
                            modifier = Modifier.fillMaxWidth().testTag("clear_all_data_button")
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Clear All Data")
                        }
                    }
                }
            }

            // About Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Mujahid Accounts",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Version 1.0 • Offline-First Digital Ledger",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Safe, Reliable Khata & Stock Management for Traders and Businesses.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }

    if (showEditProfileDialog) {
        EditBusinessProfileDialog(
            name = settings.businessName,
            phone = settings.businessPhone,
            address = settings.businessAddress,
            onDismiss = { showEditProfileDialog = false },
            onConfirm = { name, phone, address ->
                viewModel.updateBusinessProfile(name, phone, address)
                showEditProfileDialog = false
            }
        )
    }

    if (showCurrencyDialog) {
        CurrencySelectionDialog(
            currentSymbol = settings.currencySymbol,
            onDismiss = { showCurrencyDialog = false },
            onSelect = { sym, code ->
                viewModel.updateCurrency(sym, code)
                showCurrencyDialog = false
            }
        )
    }

    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentCode = settings.languageCode,
            onDismiss = { showLanguageDialog = false },
            onSelect = { code ->
                viewModel.updateLanguage(code)
                showLanguageDialog = false
            }
        )
    }

    if (showClearDataConfirm) {
        AlertDialog(
            onDismissRequest = { showClearDataConfirm = false },
            title = { Text("Clear All Ledger Data?") },
            text = { Text("This will permanently delete all parties, ledger transactions, cashbook records, and inventory products.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllData()
                        showClearDataConfirm = false
                        Toast.makeText(context, "All data wiped.", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DebitRed)
                ) {
                    Text("Delete Everything")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun EditBusinessProfileDialog(
    name: String,
    phone: String,
    address: String,
    onDismiss: () -> Unit,
    onConfirm: (name: String, phone: String, address: String) -> Unit
) {
    var businessName by remember { mutableStateOf(name) }
    var businessPhone by remember { mutableStateOf(phone) }
    var businessAddress by remember { mutableStateOf(address) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Business Profile", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = businessName,
                    onValueChange = { businessName = it },
                    label = { Text("Business / Shop Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_business_name_input")
                )
                OutlinedTextField(
                    value = businessPhone,
                    onValueChange = { businessPhone = it },
                    label = { Text("Business Phone") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = businessAddress,
                    onValueChange = { businessAddress = it },
                    label = { Text("Shop / Office Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (businessName.isNotBlank()) {
                        onConfirm(businessName.trim(), businessPhone.trim(), businessAddress.trim())
                    }
                },
                enabled = businessName.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun CurrencySelectionDialog(
    currentSymbol: String,
    onDismiss: () -> Unit,
    onSelect: (symbol: String, code: String) -> Unit
) {
    val currencies = listOf(
        Pair("Rs", "PKR - Pakistani Rupee"),
        Pair("$", "USD - US Dollar"),
        Pair("د.إ", "AED - UAE Dirham"),
        Pair("ر.س", "SAR - Saudi Riyal"),
        Pair("₹", "INR - Indian Rupee"),
        Pair("€", "EUR - Euro"),
        Pair("£", "GBP - British Pound")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Currency") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                currencies.forEach { (symbol, name) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(symbol, name.take(3)) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (currentSymbol == symbol) EmeraldGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(name, fontWeight = FontWeight.Medium)
                            Text(symbol, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
private fun LanguageSelectionDialog(
    currentCode: String,
    onDismiss: () -> Unit,
    onSelect: (code: String) -> Unit
) {
    val languages = listOf(
        Pair("en", "English (Default)"),
        Pair("ur", "اردو (Urdu)"),
        Pair("roman", "Roman Urdu (Hinglish/Urduish)")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose Language") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                languages.forEach { (code, name) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(code) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (currentCode == code) EmeraldGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(name, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}
