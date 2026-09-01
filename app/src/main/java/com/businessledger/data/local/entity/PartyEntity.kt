package com.businessledger.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.businessledger.domain.model.Party
import com.businessledger.domain.model.PartyType

@Entity(tableName = "parties")
data class PartyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phone: String,
    val address: String,
    val partyType: String, // "CUSTOMER", "SUPPLIER"
    val openingBalance: Double,
    val currentBalance: Double,
    val notes: String,
    val createdAt: Long,
    val updatedAt: Long
) {
    fun toDomain(): Party = Party(
        id = id,
        name = name,
        phone = phone,
        address = address,
        partyType = try { PartyType.valueOf(partyType) } catch (e: Exception) { PartyType.CUSTOMER },
        openingBalance = openingBalance,
        currentBalance = currentBalance,
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    companion object {
        fun fromDomain(party: Party): PartyEntity = PartyEntity(
            id = party.id,
            name = party.name,
            phone = party.phone,
            address = party.address,
            partyType = party.partyType.name,
            openingBalance = party.openingBalance,
            currentBalance = party.currentBalance,
            notes = party.notes,
            createdAt = party.createdAt,
            updatedAt = party.updatedAt
        )
    }
}
