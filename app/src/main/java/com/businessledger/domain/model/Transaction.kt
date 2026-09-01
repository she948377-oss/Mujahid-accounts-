package com.businessledger.domain.model

data class Transaction(
    val id: Long = 0,
    val partyId: Long,
    val partyName: String = "",
    val amount: Double,
    val type: TransactionType,
    val date: Long = System.currentTimeMillis(),
    val description: String = "",
    val invoiceNumber: String = "",
    val paymentMethod: String = "Cash",
    val billImagePath: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
