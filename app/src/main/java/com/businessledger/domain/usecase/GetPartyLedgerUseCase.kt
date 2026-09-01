package com.businessledger.domain.usecase

import com.businessledger.data.repository.LedgerRepository
import com.businessledger.domain.model.Party
import com.businessledger.domain.model.Transaction
import com.businessledger.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

data class PartyLedgerStatement(
    val party: Party?,
    val transactions: List<LedgerTransactionItem>,
    val totalGave: Double,
    val totalGot: Double,
    val netBalance: Double
)

data class LedgerTransactionItem(
    val transaction: Transaction,
    val runningBalance: Double
)

class GetPartyLedgerUseCase(
    private val repository: LedgerRepository
) {
    operator fun invoke(partyId: Long): Flow<PartyLedgerStatement> {
        return combine(
            repository.getPartyById(partyId),
            repository.getTransactionsByParty(partyId)
        ) { party, rawTransactions ->
            if (party == null) {
                return@combine PartyLedgerStatement(null, emptyList(), 0.0, 0.0, 0.0)
            }

            // Sort chronologically ascending to calculate running balance accurately
            val chronologicalList = rawTransactions.sortedWith(compareBy({ it.date }, { it.id }))
            var running = party.openingBalance
            var totalGave = 0.0
            var totalGot = 0.0

            val items = chronologicalList.map { tx ->
                if (tx.type == TransactionType.GAVE) {
                    running += tx.amount
                    totalGave += tx.amount
                } else {
                    running -= tx.amount
                    totalGot += tx.amount
                }
                LedgerTransactionItem(
                    transaction = tx,
                    runningBalance = running
                )
            }

            // Return in reverse order (newest first) for UI display, keeping each item's computed running balance
            PartyLedgerStatement(
                party = party.copy(currentBalance = running),
                transactions = items.reversed(),
                totalGave = totalGave,
                totalGot = totalGot,
                netBalance = running
            )
        }
    }
}
