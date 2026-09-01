package com.businessledger.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.businessledger.data.local.entity.CashEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CashEntryDao {
    @Query("SELECT * FROM cash_entries ORDER BY date DESC, id DESC")
    fun getAllCashEntries(): Flow<List<CashEntryEntity>>

    @Query("SELECT * FROM cash_entries WHERE type = :type ORDER BY date DESC, id DESC")
    fun getCashEntriesByType(type: String): Flow<List<CashEntryEntity>>

    @Query("SELECT * FROM cash_entries WHERE id = :id LIMIT 1")
    fun getCashEntryById(id: Long): Flow<CashEntryEntity?>

    @Query("SELECT * FROM cash_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC, id DESC")
    fun getCashEntriesByDateRange(startDate: Long, endDate: Long): Flow<List<CashEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCashEntry(entry: CashEntryEntity): Long

    @Update
    suspend fun updateCashEntry(entry: CashEntryEntity)

    @Delete
    suspend fun deleteCashEntry(entry: CashEntryEntity)

    @Query("DELETE FROM cash_entries WHERE id = :id")
    suspend fun deleteCashEntryById(id: Long)
}
