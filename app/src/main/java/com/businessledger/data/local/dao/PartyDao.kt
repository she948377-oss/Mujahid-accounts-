package com.businessledger.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.businessledger.data.local.entity.PartyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PartyDao {
    @Query("SELECT * FROM parties ORDER BY updatedAt DESC")
    fun getAllParties(): Flow<List<PartyEntity>>

    @Query("SELECT * FROM parties WHERE partyType = :partyType ORDER BY updatedAt DESC")
    fun getPartiesByType(partyType: String): Flow<List<PartyEntity>>

    @Query("SELECT * FROM parties WHERE id = :id LIMIT 1")
    fun getPartyById(id: Long): Flow<PartyEntity?>

    @Query("SELECT * FROM parties WHERE id = :id LIMIT 1")
    suspend fun getPartyByIdDirect(id: Long): PartyEntity?

    @Query("SELECT * FROM parties WHERE name LIKE '%' || :query || '%' OR phone LIKE '%' || :query || '%' ORDER BY updatedAt DESC")
    fun searchParties(query: String): Flow<List<PartyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParty(party: PartyEntity): Long

    @Update
    suspend fun updateParty(party: PartyEntity)

    @Query("UPDATE parties SET currentBalance = :newBalance, updatedAt = :updatedAt WHERE id = :partyId")
    suspend fun updatePartyBalance(partyId: Long, newBalance: Double, updatedAt: Long = System.currentTimeMillis())

    @Delete
    suspend fun deleteParty(party: PartyEntity)

    @Query("DELETE FROM parties WHERE id = :id")
    suspend fun deletePartyById(id: Long)

    @Query("SELECT COUNT(*) FROM parties WHERE partyType = 'CUSTOMER'")
    fun getCustomerCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM parties WHERE partyType = 'SUPPLIER'")
    fun getSupplierCount(): Flow<Int>
}
