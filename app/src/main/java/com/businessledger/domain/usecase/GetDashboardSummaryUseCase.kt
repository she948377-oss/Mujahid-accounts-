package com.businessledger.domain.usecase

import com.businessledger.data.repository.LedgerRepository
import com.businessledger.domain.model.CashEntryType
import com.businessledger.domain.model.DashboardSummary
import com.businessledger.domain.model.PartyType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Calendar

class GetDashboardSummaryUseCase(
    private val repository: LedgerRepository
) {
    operator fun invoke(): Flow<DashboardSummary> {
        return combine(
            repository.allParties,
            repository.allCashEntries,
            repository.allProducts
        ) { parties, cashEntries, products ->
            var totalReceivable = 0.0
            var totalPayable = 0.0
            var customerCount = 0
            var supplierCount = 0

            for (party in parties) {
                if (party.partyType == PartyType.CUSTOMER) {
                    customerCount++
                } else {
                    supplierCount++
                }

                if (party.currentBalance > 0) {
                    totalReceivable += party.currentBalance
                } else if (party.currentBalance < 0) {
                    totalPayable += Math.abs(party.currentBalance)
                }
            }

            // Calculate today's start and end epoch
            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val startOfDay = cal.timeInMillis

            var todayCashIn = 0.0
            var todayCashOut = 0.0

            for (entry in cashEntries) {
                if (entry.date >= startOfDay) {
                    if (entry.type == CashEntryType.CASH_IN) {
                        todayCashIn += entry.amount
                    } else {
                        todayCashOut += entry.amount
                    }
                }
            }

            var lowStockCount = 0
            var stockValuation = 0.0
            for (product in products) {
                if (product.stockQuantity <= product.minStockAlert) {
                    lowStockCount++
                }
                stockValuation += (product.purchasePrice * product.stockQuantity)
            }

            DashboardSummary(
                totalReceivable = totalReceivable,
                totalPayable = totalPayable,
                netBalance = totalReceivable - totalPayable,
                todayCashIn = todayCashIn,
                todayCashOut = todayCashOut,
                todayNetCash = todayCashIn - todayCashOut,
                totalCustomersCount = customerCount,
                totalSuppliersCount = supplierCount,
                totalProductsCount = products.size,
                lowStockCount = lowStockCount,
                totalStockValuation = stockValuation
            )
        }
    }
}
