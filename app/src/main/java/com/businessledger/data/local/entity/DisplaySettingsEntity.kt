package com.businessledger.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "display_settings")
data class DisplaySettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val businessName: String = "Mujahid Accounts",
    val businessPhone: String = "",
    val businessAddress: String = "",
    val currencySymbol: String = "Rs.",
    val currencyCode: String = "PKR",
    val language: String = "Bilingual", // "English", "Urdu", "Bilingual"
    val languageCode: String = "bilingual",
    val showBalanceInHeader: Boolean = true,
    val compactView: Boolean = false,
    val showDecimals: Boolean = true,
    val defaultPaymentMode: String = "Cash",
    val isDarkMode: Boolean = true
)
