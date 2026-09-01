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

    // Format currency like: Rs. 15,000 or 15,000 روپے
    fun formatCurrency(amount: Double, symbol: String = "Rs.", languageMode: LanguageMode = LanguageMode.BILINGUAL): String {
        val formatter = NumberFormat.getNumberInstance(Locale.US)
        formatter.minimumFractionDigits = 0
        formatter.maximumFractionDigits = 2
        val formattedNumber = formatter.format(Math.abs(amount))

        return when (languageMode) {
            LanguageMode.URDU -> "$formattedNumber $symbol"
            else -> "$symbol $formattedNumber"
        }
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

    // Key business terms localized
    fun getText(key: String, mode: LanguageMode = LanguageMode.BILINGUAL): String {
        val translation = translations[key] ?: return key
        return when (mode) {
            LanguageMode.ENGLISH -> translation.en
            LanguageMode.URDU -> translation.ur
            LanguageMode.BILINGUAL -> "${translation.en} (${translation.ur})"
        }
    }

    fun getUrduOnly(key: String): String = translations[key]?.ur ?: key
    fun getEnglishOnly(key: String): String = translations[key]?.en ?: key
    fun getRomanUrdu(key: String): String = translations[key]?.roman ?: key

    private data class Translation(val en: String, val ur: String, val roman: String)

    private val translations = mapOf(
        "app_title" to Translation("Mujahid Accounts", "مجاہد اکاونٹس", "Mujahid Accounts"),
        "dashboard" to Translation("Dashboard", "ڈیش بورڈ", "Dashboard"),
        "parties" to Translation("Parties Khata", "کھاتہ پارٹیز", "Khata Parties"),
        "cashbook" to Translation("Cash Book", "روکڑ کھاتہ", "Rokar Khata"),
        "inventory" to Translation("Stock & Inventory", "اسٹاک اور مال", "Stock & Maal"),
        "reports" to Translation("Reports & Statements", "رپورٹس اور حساب کتاب", "Reports & Hisaab"),
        "settings" to Translation("Settings", "ترتیبات", "Settings"),
        "receivable" to Translation("To Receive (Lena Hai)", "لینا ہے (بقایا)", "Lena Hai"),
        "payable" to Translation("To Pay (Dena Hai)", "دینا ہے (واجب الادا)", "Dena Hai"),
        "net_balance" to Translation("Net Balance", "کل بقایا بیلنس", "Kul Balance"),
        "you_gave" to Translation("You Gave (Udhaar)", "آپ نے دیا (ادھار)", "Aap Ne Diya"),
        "you_got" to Translation("You Got (Payment)", "آپ کو ملا (وصولی)", "Aap Ko Mila"),
        "cash_in" to Translation("Cash In (Aamad)", "کیش وصولی (آمد)", "Cash Aamad"),
        "cash_out" to Translation("Cash Out (Kharch)", "کیش ادائیگی (خرچ)", "Cash Kharch"),
        "today_cash" to Translation("Today's Cash Flow", "آج کا کیش", "Aaj Ka Cash"),
        "customer" to Translation("Customer", "گاہک / خریدار", "Grahak"),
        "supplier" to Translation("Supplier", "سپلائر / بیوپاری", "Supplier"),
        "all_parties" to Translation("All Parties", "تمام کھاتے", "Tamam Khate"),
        "add_party" to Translation("Add New Party", "نیا کھاتہ شامل کریں", "Naya Khata"),
        "add_transaction" to Translation("Add Entry", "اندراج کریں", "Indraaj Karen"),
        "add_product" to Translation("Add Product", "نئی آئٹم شامل کریں", "Nayi Item"),
        "add_cash_entry" to Translation("Add Cash Entry", "کیش انٹری درج کریں", "Cash Entry"),
        "low_stock" to Translation("Low Stock Alert", "کم اسٹاک وارننگ", "Kam Stock"),
        "stock_valuation" to Translation("Total Stock Value", "کل مال کی مالیت", "Kul Maal Qimat"),
        "search" to Translation("Search by name, phone or bill", "نام یا فون سے تلاش کریں", "Talash Karen"),
        "phone" to Translation("Phone Number", "فون نمبر", "Phone Number"),
        "address" to Translation("Address / Location", "پتہ / دکان", "Pata"),
        "opening_balance" to Translation("Opening Balance", "سابقہ بقایا", "Sabiqa Baqaya"),
        "running_balance" to Translation("Running Balance", "میزان / موجودہ بقایا", "Meezan / Baqaya"),
        "note" to Translation("Notes / Remarks", "تفصیل / ریمارکس", "Tafseel"),
        "invoice_no" to Translation("Bill / Invoice #", "بل نمبر", "Bill Number"),
        "payment_mode" to Translation("Payment Mode", "طریقہ ادائیگی", "Tariqa"),
        "category" to Translation("Category", "کیٹیگری", "Category"),
        "date_time" to Translation("Date & Time", "تاریخ اور وقت", "Tareekh"),
        "save" to Translation("Save", "محفوظ کریں", "Mehfooz Karen"),
        "cancel" to Translation("Cancel", "منسوخ", "Mansookh"),
        "delete" to Translation("Delete", "حذف کریں", "Khatam Karen"),
        "edit" to Translation("Edit", "تبدیل کریں", "Tabdeel Karen"),
        "share_statement" to Translation("Share Statement (WhatsApp/SMS)", "کھاتہ شیئر کریں", "Khata Share Karen"),
        "send_reminder" to Translation("Payment Reminder", "ادائیگی کی یاد دہانی", "Taqaza Reminder")
    )

    // Generate formatted WhatsApp / SMS message text for sending payment reminders or bills to customer/supplier
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
            محترم $partyName صاحب،
            $businessName کی طرف سے سلام۔
            آپ کے کھاتے کا بقایا بیلنس $balanceFormatted (لینا ہے) ہے۔
            ${if (lastTransactionDate != null) "آخری لین دین: $lastTransactionDate\n" else ""}براہ کرم ادائیگی جلد از جلد ممکن بنائیں۔
            شکریہ!
            
            ---
            Dear $partyName,
            Greetings from $businessName.
            Your outstanding balance is $balanceFormatted (Receivable).
            Please arrange the payment at your earliest convenience.
            Thank you!
            """.trimIndent()
        } else if (currentBalance < 0) {
            """
            محترم $partyName صاحب،
            $businessName کی طرف سے آپ کا پیشگی/ادائیگی بیلنس $balanceFormatted (دینا ہے) ہے۔
            شکریہ!
            
            ---
            Dear $partyName,
            Balance with $businessName is $balanceFormatted (Advance/Payable).
            Thank you!
            """.trimIndent()
        } else {
            """
            محترم $partyName صاحب،
            $businessName کی طرف سے آپ کا کھاتہ مکمل طور پر کلیئر اور نل (0) ہے۔
            شکریہ!
            """.trimIndent()
        }
    }
}
