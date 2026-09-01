package com.businessledger.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.businessledger.domain.model.Product

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val sku: String,
    val purchasePrice: Double,
    val sellingPrice: Double,
    val stockQuantity: Double,
    val unit: String,
    val category: String,
    val minStockAlert: Double,
    val updatedAt: Long
) {
    fun toDomain(): Product = Product(
        id = id,
        name = name,
        sku = sku,
        purchasePrice = purchasePrice,
        sellingPrice = sellingPrice,
        stockQuantity = stockQuantity,
        unit = unit,
        category = category,
        minStockAlert = minStockAlert,
        updatedAt = updatedAt
    )

    companion object {
        fun fromDomain(product: Product): ProductEntity = ProductEntity(
            id = product.id,
            name = product.name,
            sku = product.sku,
            purchasePrice = product.purchasePrice,
            sellingPrice = product.sellingPrice,
            stockQuantity = product.stockQuantity,
            unit = product.unit,
            category = product.category,
            minStockAlert = product.minStockAlert,
            updatedAt = product.updatedAt
        )
    }
}
