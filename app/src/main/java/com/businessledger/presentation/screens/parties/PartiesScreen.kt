package com.businessledger.presentation.screens.parties

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.businessledger.domain.model.Party
import com.businessledger.domain.model.PartyType
import com.businessledger.presentation.theme.CreditGreen
import com.businessledger.presentation.theme.CreditGreenBg
import com.businessledger.presentation.theme.DebitRed
import com.businessledger.presentation.theme.DebitRedBg
import com.businessledger.presentation.theme.EmeraldGreen
import com.businessledger.presentation.theme.FintechDarkBorder
import com.businessledger.presentation.viewmodel.PartyFilterTab
import com.businessledger.presentation.viewmodel.PartiesUiState
import com.businessledger.presentation.viewmodel.PartiesViewModel
import com.businessledger.presentation.viewmodel.PartySortOption
import com.businessledger.utils.DisplaySettingsManager
import com.businessledger.utils.UrduLocalization

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartiesScreen(
    viewModel: PartiesViewModel,
    onPartyClick: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val settings by viewModel.displaySettings.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize().testTag("parties_screen"),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.PersonAdd, contentDescription = null) },
                text = { Text("Add Party") },
                containerColor = EmeraldGreen,
                contentColor = Color.White,
                modifier = Modifier.padding(bottom = 72.dp).testTag("add_party_fab")
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header summary bar
            PartiesTopSummary(
                receivable = uiState.totalReceivable,
                payable = uiState.totalPayable,
                settings = settings
            )

            // Search Bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search by name, phone or address...") },
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
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("parties_search_input"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Primary Tabs: All, Customers, Suppliers
            val tabs = listOf(
                PartyFilterTab.ALL to "All (${uiState.parties.size})",
                PartyFilterTab.CUSTOMERS to "Customers",
                PartyFilterTab.SUPPLIERS to "Suppliers"
            )
            val selectedTabIndex = when (uiState.filterTab) {
                PartyFilterTab.ALL -> 0
                PartyFilterTab.CUSTOMERS -> 1
                PartyFilterTab.SUPPLIERS -> 2
            }

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
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Party List
            if (uiState.parties.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No parties found",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tap '+ Add Party' to create a customer or supplier khata.",
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
                    items(uiState.parties, key = { it.id }) { party ->
                        PartyListItem(
                            party = party,
                            settings = settings,
                            onClick = { onPartyClick(party.id) },
                            onCall = {
                                if (party.phone.isNotEmpty()) {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${party.phone}"))
                                    context.startActivity(intent)
                                }
                            },
                            onShare = {
                                val message = UrduLocalization.generatePartyShareMessage(
                                    businessName = settings.businessName,
                                    partyName = party.name,
                                    currentBalance = party.currentBalance,
                                    currencySymbol = settings.currencySymbol,
                                    lastTransactionDate = UrduLocalization.formatDateOnly(party.updatedAt)
                                )
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, message)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Khata Statement"))
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddPartyFullDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, phone, address, type, openingBal, notes ->
                viewModel.addParty(name, phone, address, type, openingBal, notes)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun PartiesTopSummary(
    receivable: Double,
    payable: Double,
    settings: com.businessledger.data.local.entity.DisplaySettingsEntity
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, FintechDarkBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Total Receivable",
                    style = MaterialTheme.typography.labelSmall,
                    color = CreditGreen,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = DisplaySettingsManager.formatPrice(receivable, settings),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = CreditGreen
                )
            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(38.dp)
                    .background(FintechDarkBorder)
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Total Payable",
                    style = MaterialTheme.typography.labelSmall,
                    color = DebitRed,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = DisplaySettingsManager.formatPrice(payable, settings),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = DebitRed
                )
            }
        }
    }
}

@Composable
private fun PartyListItem(
    party: Party,
    settings: com.businessledger.data.local.entity.DisplaySettingsEntity,
    onClick: () -> Unit,
    onCall: () -> Unit,
    onShare: () -> Unit
) {
    val isCustomer = party.partyType == PartyType.CUSTOMER
    val balance = party.currentBalance
    val isReceivable = balance > 0
    val isPayable = balance < 0

    val balanceColor = when {
        isReceivable -> CreditGreen
        isPayable -> DebitRed
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val balanceLabel = when {
        isReceivable -> "To Receive"
        isPayable -> "To Pay"
        else -> "Settled (0.00)"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("party_item_${party.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, FintechDarkBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Initial Circle Avatar
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            if (isCustomer) EmeraldGreen.copy(alpha = 0.15f) else Color(0xFFD97706).copy(alpha = 0.15f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = party.name.take(2).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = if (isCustomer) EmeraldGreen else Color(0xFFD97706),
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = party.name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (isCustomer) EmeraldGreen.copy(alpha = 0.1f) else Color(0xFFD97706).copy(alpha = 0.1f)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isCustomer) "Customer" else "Supplier",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = if (isCustomer) EmeraldGreen else Color(0xFFD97706),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    if (party.phone.isNotEmpty()) {
                        Text(
                            text = party.phone,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = "Updated: ${UrduLocalization.formatDateOnly(party.updatedAt)}",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }

            // Balance and Actions
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = DisplaySettingsManager.formatPrice(Math.abs(balance), settings),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = balanceColor
                )
                Text(
                    text = balanceLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = balanceColor,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (party.phone.isNotEmpty()) {
                        IconButton(
                            onClick = onCall,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Call",
                                modifier = Modifier.size(14.dp),
                                tint = EmeraldGreen
                            )
                        }
                    }

                    IconButton(
                        onClick = onShare,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddPartyFullDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        name: String,
        phone: String,
        address: String,
        type: PartyType,
        openingBal: Double,
        notes: String
    ) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var partyType by remember { mutableStateOf(PartyType.CUSTOMER) }
    var openingBalanceStr by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Party Khata",
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
                    label = { Text("Party Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("dialog_party_name_input")
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("dialog_party_phone_input")
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Shop / Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = openingBalanceStr,
                    onValueChange = { openingBalanceStr = it },
                    label = { Text("Opening Balance") },
                    placeholder = { Text("e.g. 5000 (Positive for Receivable)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("dialog_party_balance_input")
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / Remarks") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val bal = openingBalanceStr.toDoubleOrNull() ?: 0.0
                        onConfirm(name, phone, address, partyType, bal, notes)
                    }
                },
                enabled = name.isNotBlank(),
                modifier = Modifier.testTag("dialog_save_party_button")
            ) {
                Text("Save Khata")
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
