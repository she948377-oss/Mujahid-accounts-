package com.businessledger.domain.model

data class Product(
    val id: Long = 0,
    val name: String,
    val sku: String = "",
    val purchasePrice: Double = 0.0,
    val sellingPrice: Double = 0.0,
    val stockQuantity: Double = 0.0,
    val unit: String = "Pcs",
    val category: String = "General",
    val minStockAlert: Double = 5.0,
    val updatedAt: Long = System.currentTimeMillis()
)
