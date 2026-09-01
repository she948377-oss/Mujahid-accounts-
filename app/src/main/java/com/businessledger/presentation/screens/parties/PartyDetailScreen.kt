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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.businessledger.domain.model.Transaction
import com.businessledger.domain.model.TransactionType
import com.businessledger.domain.usecase.LedgerTransactionItem
import com.businessledger.presentation.theme.CreditGreen
import com.businessledger.presentation.theme.CreditGreenBg
import com.businessledger.presentation.theme.DebitRed
import com.businessledger.presentation.theme.DebitRedBg
import com.businessledger.presentation.theme.EmeraldDark
import com.businessledger.presentation.theme.EmeraldGreen
import com.businessledger.presentation.viewmodel.PartyDetailViewModel
import com.businessledger.utils.DisplaySettingsManager
import com.businessledger.utils.UrduLocalization

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartyDetailScreen(
    partyId: Long,
    viewModel: PartyDetailViewModel,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(partyId) {
        viewModel.setPartyId(partyId)
    }

    val statement by viewModel.ledgerStatement.collectAsStateWithLifecycle()
    val settings by viewModel.displaySettings.collectAsStateWithLifecycle()
    val party = statement.party
    val context = LocalContext.current

    var showTransactionDialog by remember { mutableStateOf(false) }
    var activeTxType by remember { mutableStateOf(TransactionType.GAVE) }
    var transactionToDelete by remember { mutableStateOf<Long?>(null) }
    var showEditPartyDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = party?.name ?: "Party Ledger",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        if (party?.phone?.isNotEmpty() == true) {
                            Text(
                                text = party.phone,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("party_detail_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (party?.phone?.isNotEmpty() == true) {
                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${party.phone}"))
                                context.startActivity(intent)
                            }
                        ) {
                            Icon(Icons.Default.Call, contentDescription = "Call", tint = EmeraldGreen)
                        }
                    }
                    IconButton(
                        onClick = {
                            if (party != null) {
                                val message = UrduLocalization.generatePartyShareMessage(
                                    businessName = settings.businessName,
                                    partyName = party.name,
                                    currentBalance = party.currentBalance,
                                    currencySymbol = settings.currencySymbol,
                                    lastTransactionDate = if (statement.transactions.isNotEmpty()) {
                                        UrduLocalization.formatDateOnly(statement.transactions.first().transaction.date)
                                    } else null
                                )
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, message)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Statement"))
                            }
                        }
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(
                        onClick = { showEditPartyDialog = true }
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Party")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            // Sticky Bottom Action Bar (You Gave / You Got)
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
                    // You Gave (Red)
                    Button(
                        onClick = {
                            activeTxType = TransactionType.GAVE
                            showTransactionDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DebitRed,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("you_gave_button")
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("YOU GAVE", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Goods / Cash Given", fontSize = 10.sp)
                        }
                    }

                    // You Got (Green)
                    Button(
                        onClick = {
                            activeTxType = TransactionType.GOT
                            showTransactionDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CreditGreen,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("you_got_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("YOU GOT", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Payment Received", fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .testTag("party_detail_list"),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Party Info & Summary Card
            item {
                if (party != null) {
                    PartyDetailHeaderCard(
                        party = party,
                        statement = statement,
                        settings = settings
                    )
                }
            }

            // Transaction Table Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ledger Entries (${statement.transactions.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Running Balance",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (statement.transactions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("No transactions yet", fontWeight = FontWeight.SemiBold)
                            Text(
                                "Record what you gave or received below to start this ledger.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(statement.transactions, key = { it.transaction.id }) { item ->
                    LedgerTransactionRow(
                        item = item,
                        currencySymbol = settings.currencySymbol,
                        onDelete = { transactionToDelete = item.transaction.id }
                    )
                }
            }
        }
    }

    if (showTransactionDialog) {
        RecordTransactionDialog(
            type = activeTxType,
            currencySymbol = settings.currencySymbol,
            onDismiss = { showTransactionDialog = false },
            onConfirm = { amount, description, invoiceNo, paymentMethod ->
                viewModel.addTransaction(
                    amount = amount,
                    type = activeTxType,
                    description = description,
                    invoiceNumber = invoiceNo,
                    paymentMethod = paymentMethod
                )
                showTransactionDialog = false
            }
        )
    }

    if (transactionToDelete != null) {
        AlertDialog(
            onDismissRequest = { transactionToDelete = null },
            title = { Text("Delete Entry") },
            text = { Text("Are you sure you want to delete this ledger entry? Running balance will automatically recalculate.") },
            confirmButton = {
                Button(
                    onClick = {
                        val id = transactionToDelete
                        if (id != null) viewModel.deleteTransaction(id)
                        transactionToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DebitRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { transactionToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showEditPartyDialog && party != null) {
        EditPartyDialog(
            party = party,
            onDismiss = { showEditPartyDialog = false },
            onConfirm = { updated ->
                viewModel.updateParty(updated)
                showEditPartyDialog = false
            }
        )
    }
}

@Composable
private fun PartyDetailHeaderCard(
    party: Party,
    statement: com.businessledger.domain.usecase.PartyLedgerStatement,
    settings: com.businessledger.data.local.entity.DisplaySettingsEntity
) {
    val netBalance = statement.netBalance
    val isReceivable = netBalance > 0
    val isPayable = netBalance < 0
    val balanceColor = if (isReceivable) CreditGreen else if (isPayable) DebitRed else MaterialTheme.colorScheme.onSurfaceVariant
    val balanceLabel = if (isReceivable) "You will receive (Receivable)" else if (isPayable) "You will pay (Payable)" else "Settled / Clear (0.00)"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Net Balance Display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Net Balance",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = DisplaySettingsManager.formatPrice(Math.abs(netBalance), settings),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = balanceColor
                    )
                    Text(
                        text = balanceLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = balanceColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isReceivable) CreditGreenBg else DebitRedBg)
                        .padding(12.dp)
                ) {
                    Text(
                        text = if (party.partyType == PartyType.CUSTOMER) "Customer" else "Supplier",
                        fontWeight = FontWeight.Bold,
                        color = if (party.partyType == PartyType.CUSTOMER) EmeraldGreen else Color(0xFFD97706),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Total Gave & Total Got Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Total Gave",
                        style = MaterialTheme.typography.labelSmall,
                        color = DebitRed
                    )
                    Text(
                        text = "${settings.currencySymbol} ${statement.totalGave}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = DebitRed
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(30.dp)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Total Got",
                        style = MaterialTheme.typography.labelSmall,
                        color = CreditGreen
                    )
                    Text(
                        text = "${settings.currencySymbol} ${statement.totalGot}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = CreditGreen
                    )
                }
            }

            if (party.address.isNotEmpty() || party.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                if (party.address.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(party.address, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun LedgerTransactionRow(
    item: LedgerTransactionItem,
    currencySymbol: String,
    onDelete: () -> Unit
) {
    val tx = item.transaction
    val isGave = tx.type == TransactionType.GAVE
    val amountColor = if (isGave) DebitRed else CreditGreen

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = UrduLocalization.formatDate(tx.date),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = tx.description.ifEmpty { if (isGave) "Goods / Cash Given" else "Payment Received" },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (tx.invoiceNumber.isNotEmpty()) {
                        Text(
                            text = "Bill #${tx.invoiceNumber} • ${tx.paymentMethod}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${if (isGave) "-" else "+"}$currencySymbol ${tx.amount}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = amountColor
                    )
                    Text(
                        text = "Balance: $currencySymbol ${item.runningBalance}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
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
private fun RecordTransactionDialog(
    type: TransactionType,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, description: String, invoiceNo: String, paymentMethod: String) -> Unit
) {
    var amountStr by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var invoiceNo by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("Cash") }

    val isGave = type == TransactionType.GAVE
    val methods = listOf("Cash", "Bank Transfer", "JazzCash", "EasyPaisa", "Cheque")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isGave) "You Gave (-)" else "You Got (+)",
                color = if (isGave) DebitRed else CreditGreen,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Amount ($currencySymbol) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("transaction_amount_input")
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Notes / Item Description") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("transaction_desc_input")
                )

                OutlinedTextField(
                    value = invoiceNo,
                    onValueChange = { invoiceNo = it },
                    label = { Text("Bill / Invoice Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Payment Method:", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    methods.take(3).forEach { method ->
                        FilterChip(
                            selected = paymentMethod == method,
                            onClick = { paymentMethod = method },
                            label = { Text(method, fontSize = 11.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountStr.toDoubleOrNull() ?: 0.0
                    if (amt > 0) {
                        onConfirm(amt, description, invoiceNo, paymentMethod)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isGave) DebitRed else CreditGreen
                ),
                enabled = (amountStr.toDoubleOrNull() ?: 0.0) > 0,
                modifier = Modifier.testTag("save_transaction_button")
            ) {
                Text("Save Entry")
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
private fun EditPartyDialog(
    party: Party,
    onDismiss: () -> Unit,
    onConfirm: (Party) -> Unit
) {
    var name by remember { mutableStateOf(party.name) }
    var phone by remember { mutableStateOf(party.phone) }
    var address by remember { mutableStateOf(party.address) }
    var notes by remember { mutableStateOf(party.notes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Party Details") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Party Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address / Shop") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(
                            party.copy(
                                name = name.trim(),
                                phone = phone.trim(),
                                address = address.trim(),
                                notes = notes.trim(),
                                updatedAt = System.currentTimeMillis()
                            )
                        )
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
