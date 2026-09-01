package com.businessledger.domain.model

data class CashEntry(
    val id: Long = 0,
    val type: CashEntryType,
    val amount: Double,
    val category: String = "General",
    val description: String = "",
    val paymentMode: String = "Cash",
    val date: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)
