package com.businessledger.utils

import com.businessledger.data.local.entity.DisplaySettingsEntity

object DisplaySettingsManager {
    fun parseLanguageMode(langStr: String): UrduLocalization.LanguageMode {
        return when (langStr.lowercase()) {
            "english" -> UrduLocalization.LanguageMode.ENGLISH
            "urdu" -> UrduLocalization.LanguageMode.URDU
            else -> UrduLocalization.LanguageMode.BILINGUAL
        }
    }

    fun formatPrice(amount: Double, settings: DisplaySettingsEntity?): String {
        val symbol = settings?.currencySymbol ?: "Rs."
        val lang = parseLanguageMode(settings?.language ?: "Bilingual")
        return UrduLocalization.formatCurrency(amount, symbol, lang)
    }
}
