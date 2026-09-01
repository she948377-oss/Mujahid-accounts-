package com.businessledger.domain.model

data class Party(
    val id: Long = 0,
    val name: String,
    val phone: String = "",
    val address: String = "",
    val partyType: PartyType = PartyType.CUSTOMER,
    val openingBalance: Double = 0.0,
    val currentBalance: Double = 0.0,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
