package com.businessledger.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object UrduLocalization {

    enum class LanguageMode {
        ENGLISH,
        URDU,
        BILINGUAL
    }

    // Format currency like: Rs. 15,000
    fun formatCurrency(amount: Double, symbol: String = "Rs.", languageMode: LanguageMode = LanguageMode.ENGLISH): String {
        val formatter = NumberFormat.getNumberInstance(Locale.US)
        formatter.minimumFractionDigits = 0
        formatter.maximumFractionDigits = 2
        val formattedNumber = formatter.format(Math.abs(amount))
        return "$symbol $formattedNumber"
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatDateOnly(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatTimeOnly(timestamp: Long): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    // Key business terms
    fun getText(key: String, mode: LanguageMode = LanguageMode.ENGLISH): String {
        return translations[key]?.en ?: key
    }

    fun getUrduOnly(key: String): String = translations[key]?.en ?: key
    fun getEnglishOnly(key: String): String = translations[key]?.en ?: key
    fun getRomanUrdu(key: String): String = translations[key]?.en ?: key

    private data class Translation(val en: String, val ur: String, val roman: String)

    private val translations = mapOf(
        "app_title" to Translation("Mujahid Accounts", "Mujahid Accounts", "Mujahid Accounts"),
        "dashboard" to Translation("Dashboard", "Dashboard", "Dashboard"),
        "parties" to Translation("Parties", "Parties", "Parties"),
        "cashbook" to Translation("Cash Book", "Cash Book", "Cash Book"),
        "inventory" to Translation("Stock & Inventory", "Stock & Inventory", "Stock & Inventory"),
        "reports" to Translation("Reports & Statements", "Reports & Statements", "Reports & Statements"),
        "settings" to Translation("Settings", "Settings", "Settings"),
        "receivable" to Translation("To Receive (Receivable)", "To Receive (Receivable)", "To Receive"),
        "payable" to Translation("To Pay (Payable)", "To Pay (Payable)", "To Pay"),
        "net_balance" to Translation("Net Balance", "Net Balance", "Net Balance"),
        "you_gave" to Translation("You Gave", "You Gave", "You Gave"),
        "you_got" to Translation("You Got", "You Got", "You Got"),
        "cash_in" to Translation("Cash In", "Cash In", "Cash In"),
        "cash_out" to Translation("Cash Out", "Cash Out", "Cash Out"),
        "today_cash" to Translation("Today's Cash Flow", "Today's Cash Flow", "Today's Cash Flow"),
        "customer" to Translation("Customer", "Customer", "Customer"),
        "supplier" to Translation("Supplier", "Supplier", "Supplier"),
        "all_parties" to Translation("All Parties", "All Parties", "All Parties"),
        "add_party" to Translation("Add New Party", "Add New Party", "Add New Party"),
        "add_transaction" to Translation("Add Entry", "Add Entry", "Add Entry"),
        "add_product" to Translation("Add Product", "Add Product", "Add Product"),
        "add_cash_entry" to Translation("Add Cash Entry", "Add Cash Entry", "Add Cash Entry"),
        "low_stock" to Translation("Low Stock Alert", "Low Stock Alert", "Low Stock Alert"),
        "stock_valuation" to Translation("Total Stock Value", "Total Stock Value", "Total Stock Value"),
        "search" to Translation("Search by name, phone or bill", "Search by name, phone or bill", "Search by name, phone or bill"),
        "phone" to Translation("Phone Number", "Phone Number", "Phone Number"),
        "address" to Translation("Address / Location", "Address / Location", "Address / Location"),
        "opening_balance" to Translation("Opening Balance", "Opening Balance", "Opening Balance"),
        "running_balance" to Translation("Running Balance", "Running Balance", "Running Balance"),
        "note" to Translation("Notes / Remarks", "Notes / Remarks", "Notes / Remarks"),
        "invoice_no" to Translation("Bill / Invoice #", "Bill / Invoice #", "Bill / Invoice #"),
        "payment_mode" to Translation("Payment Mode", "Payment Mode", "Payment Mode"),
        "category" to Translation("Category", "Category", "Category"),
        "date_time" to Translation("Date & Time", "Date & Time", "Date & Time"),
        "save" to Translation("Save", "Save", "Save"),
        "cancel" to Translation("Cancel", "Cancel", "Cancel"),
        "delete" to Translation("Delete", "Delete", "Delete"),
        "edit" to Translation("Edit", "Edit", "Edit"),
        "share_statement" to Translation("Share Statement", "Share Statement", "Share Statement"),
        "send_reminder" to Translation("Payment Reminder", "Payment Reminder", "Payment Reminder")
    )

    // Generate formatted message text for sending payment reminders or bills to customer/supplier
    fun generatePartyShareMessage(
        businessName: String,
        partyName: String,
        currentBalance: Double,
        currencySymbol: String = "Rs.",
        lastTransactionDate: String? = null
    ): String {
        val balanceFormatted = formatCurrency(currentBalance, currencySymbol, LanguageMode.ENGLISH)
        return if (currentBalance > 0) {
            """
            Dear $partyName,
            Greetings from $businessName.
            Your outstanding balance is $balanceFormatted (Receivable).
            ${if (lastTransactionDate != null) "Last Transaction Date: $lastTransactionDate\n" else ""}Please arrange the payment at your earliest convenience.
            Thank you!
            """.trimIndent()
        } else if (currentBalance < 0) {
            """
            Dear $partyName,
            Balance with $businessName is $balanceFormatted (Payable/Advance).
            Thank you!
            """.trimIndent()
        } else {
            """
            Dear $partyName,
            Your account with $businessName is fully settled and cleared (0.00).
            Thank you!
            """.trimIndent()
        }
    }
}
