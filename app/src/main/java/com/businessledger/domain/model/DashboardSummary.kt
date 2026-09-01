package com.businessledger.domain.model

data class DashboardSummary(
    val totalReceivable: Double = 0.0,    // You will get (Lena hai)
    val totalPayable: Double = 0.0,       // You will give (Dena hai)
    val netBalance: Double = 0.0,         // Receivable - Payable
    val todayCashIn: Double = 0.0,        // Today's total cash in
    val todayCashOut: Double = 0.0,       // Today's total cash out
    val todayNetCash: Double = 0.0,       // Today's cash in - cash out
    val totalCustomersCount: Int = 0,
    val totalSuppliersCount: Int = 0,
    val totalProductsCount: Int = 0,
    val lowStockCount: Int = 0,
    val totalStockValuation: Double = 0.0
)
