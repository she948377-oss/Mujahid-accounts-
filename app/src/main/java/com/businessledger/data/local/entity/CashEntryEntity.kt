package com.businessledger.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.businessledger.domain.model.CashEntry
import com.businessledger.domain.model.CashEntryType

@Entity(
    tableName = "cash_entries",
    indices = [Index(value = ["date"])]
)
data class CashEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // "CASH_IN", "CASH_OUT"
    val amount: Double,
    val category: String,
    val description: String,
    val paymentMode: String,
    val date: Long,
    val createdAt: Long
) {
    fun toDomain(): CashEntry = CashEntry(
        id = id,
        type = try { CashEntryType.valueOf(type) } catch (e: Exception) { CashEntryType.CASH_IN },
        amount = amount,
        category = category,
        description = description,
        paymentMode = paymentMode,
        date = date,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(entry: CashEntry): CashEntryEntity = CashEntryEntity(
            id = entry.id,
            type = entry.type.name,
            amount = entry.amount,
            category = entry.category,
            description = entry.description,
            paymentMode = entry.paymentMode,
            date = entry.date,
            createdAt = entry.createdAt
        )
    }
}
