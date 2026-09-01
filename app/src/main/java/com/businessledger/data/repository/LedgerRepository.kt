package com.businessledger.data.repository

import com.businessledger.data.local.dao.CashEntryDao
import com.businessledger.data.local.dao.DisplaySettingsDao
import com.businessledger.data.local.dao.PartyDao
import com.businessledger.data.local.dao.ProductDao
import com.businessledger.data.local.dao.TransactionDao
import com.businessledger.data.local.entity.CashEntryEntity
import com.businessledger.data.local.entity.DisplaySettingsEntity
import com.businessledger.data.local.entity.PartyEntity
import com.businessledger.data.local.entity.ProductEntity
import com.businessledger.data.local.entity.TransactionEntity
import com.businessledger.domain.model.CashEntry
import com.businessledger.domain.model.CashEntryType
import com.businessledger.domain.model.Party
import com.businessledger.domain.model.PartyType
import com.businessledger.domain.model.Product
import com.businessledger.domain.model.Transaction
import com.businessledger.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class LedgerRepository(
    private val partyDao: PartyDao,
    private val transactionDao: TransactionDao,
    private val productDao: ProductDao,
    private val cashEntryDao: CashEntryDao,
    private val displaySettingsDao: DisplaySettingsDao
) {
    // --- Parties ---
    val allParties: Flow<List<Party>> = partyDao.getAllParties().map { entities ->
        entities.map { it.toDomain() }
    }

    fun getPartiesByType(type: PartyType): Flow<List<Party>> =
        partyDao.getPartiesByType(type.name).map { list -> list.map { it.toDomain() } }

    fun getPartyById(id: Long): Flow<Party?> =
        partyDao.getPartyById(id).map { it?.toDomain() }

    fun searchParties(query: String): Flow<List<Party>> =
        partyDao.searchParties(query).map { list -> list.map { it.toDomain() } }

    suspend fun insertParty(party: Party): Long {
        val entity = PartyEntity.fromDomain(party.copy(currentBalance = party.openingBalance))
        return partyDao.insertParty(entity)
    }

    suspend fun updateParty(party: Party) {
        val entity = PartyEntity.fromDomain(party)
        partyDao.updateParty(entity)
        recalculatePartyBalance(party.id)
    }

    suspend fun deleteParty(partyId: Long) {
        transactionDao.deleteTransactionsByPartyId(partyId)
        partyDao.deletePartyById(partyId)
    }

    suspend fun recalculatePartyBalance(partyId: Long) {
        val party = partyDao.getPartyByIdDirect(partyId) ?: return
        val transactions = transactionDao.getTransactionsByPartyDirect(partyId)

        var balance = party.openingBalance
        for (tx in transactions) {
            if (tx.type == TransactionType.GAVE.name) {
                balance += tx.amount
            } else if (tx.type == TransactionType.GOT.name) {
                balance -= tx.amount
            }
        }
        partyDao.updatePartyBalance(partyId, balance)
    }

    // --- Transactions ---
    val allTransactions: Flow<List<Transaction>> =
        combine(transactionDao.getAllTransactions(), partyDao.getAllParties()) { txEntities, partyEntities ->
            val partyMap = partyEntities.associate { it.id to it.name }
            txEntities.map { entity ->
                entity.toDomain(partyName = partyMap[entity.partyId] ?: "Unknown Party")
            }
        }

    fun getTransactionsByParty(partyId: Long): Flow<List<Transaction>> =
        transactionDao.getTransactionsByParty(partyId).map { list ->
            list.map { it.toDomain() }
        }

    suspend fun insertTransaction(transaction: Transaction): Long {
        val id = transactionDao.insertTransaction(TransactionEntity.fromDomain(transaction))
        recalculatePartyBalance(transaction.partyId)
        return id
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(TransactionEntity.fromDomain(transaction))
        recalculatePartyBalance(transaction.partyId)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(TransactionEntity.fromDomain(transaction))
        recalculatePartyBalance(transaction.partyId)
    }

    suspend fun deleteTransactionById(txId: Long, partyId: Long) {
        transactionDao.deleteTransactionById(txId)
        recalculatePartyBalance(partyId)
    }

    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<Transaction>> =
        combine(transactionDao.getTransactionsByDateRange(startDate, endDate), partyDao.getAllParties()) { txs, parties ->
            val map = parties.associate { it.id to it.name }
            txs.map { it.toDomain(partyName = map[it.partyId] ?: "") }
        }

    // --- Products (Inventory) ---
    val allProducts: Flow<List<Product>> = productDao.getAllProducts().map { list ->
        list.map { it.toDomain() }
    }

    val lowStockProducts: Flow<List<Product>> = productDao.getLowStockProducts().map { list ->
        list.map { it.toDomain() }
    }

    fun searchProducts(query: String): Flow<List<Product>> =
        productDao.searchProducts(query).map { list -> list.map { it.toDomain() } }

    suspend fun insertProduct(product: Product): Long =
        productDao.insertProduct(ProductEntity.fromDomain(product))

    suspend fun updateProduct(product: Product) =
        productDao.updateProduct(ProductEntity.fromDomain(product))

    suspend fun updateProductStock(productId: Long, newStock: Double) =
        productDao.updateProductStock(productId, newStock)

    suspend fun deleteProduct(productId: Long) =
        productDao.deleteProductById(productId)

    // --- Cash Entries ---
    val allCashEntries: Flow<List<CashEntry>> = cashEntryDao.getAllCashEntries().map { list ->
        list.map { it.toDomain() }
    }

    fun getCashEntriesByType(type: CashEntryType): Flow<List<CashEntry>> =
        cashEntryDao.getCashEntriesByType(type.name).map { list -> list.map { it.toDomain() } }

    fun getCashEntriesByDateRange(startDate: Long, endDate: Long): Flow<List<CashEntry>> =
        cashEntryDao.getCashEntriesByDateRange(startDate, endDate).map { list -> list.map { it.toDomain() } }

    suspend fun insertCashEntry(entry: CashEntry): Long =
        cashEntryDao.insertCashEntry(CashEntryEntity.fromDomain(entry))

    suspend fun updateCashEntry(entry: CashEntry) =
        cashEntryDao.updateCashEntry(CashEntryEntity.fromDomain(entry))

    suspend fun deleteCashEntry(entryId: Long) =
        cashEntryDao.deleteCashEntryById(entryId)

    // --- Display Settings ---
    val displaySettings: Flow<DisplaySettingsEntity> = displaySettingsDao.getSettings().map {
        it ?: DisplaySettingsEntity()
    }

    suspend fun updateDisplaySettings(settings: DisplaySettingsEntity) =
        displaySettingsDao.insertOrUpdate(settings)
}
