package com.businessledger.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.businessledger.domain.model.Transaction
import com.businessledger.domain.model.TransactionType

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = PartyEntity::class,
            parentColumns = ["id"],
            childColumns = ["partyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["partyId"]), Index(value = ["date"])]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val partyId: Long,
    val amount: Double,
    val type: String, // "GAVE", "GOT"
    val date: Long,
    val description: String,
    val invoiceNumber: String,
    val paymentMethod: String,
    val billImagePath: String?,
    val createdAt: Long
) {
    fun toDomain(partyName: String = ""): Transaction = Transaction(
        id = id,
        partyId = partyId,
        partyName = partyName,
        amount = amount,
        type = try { TransactionType.valueOf(type) } catch (e: Exception) { TransactionType.GAVE },
        date = date,
        description = description,
        invoiceNumber = invoiceNumber,
        paymentMethod = paymentMethod,
        billImagePath = billImagePath,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(transaction: Transaction): TransactionEntity = TransactionEntity(
            id = transaction.id,
            partyId = transaction.partyId,
            amount = transaction.amount,
            type = transaction.type.name,
            date = transaction.date,
            description = transaction.description,
            invoiceNumber = transaction.invoiceNumber,
            paymentMethod = transaction.paymentMethod,
            billImagePath = transaction.billImagePath,
            createdAt = transaction.createdAt
        )
    }
}
