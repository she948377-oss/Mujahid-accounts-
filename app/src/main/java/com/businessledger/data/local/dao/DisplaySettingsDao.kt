package com.businessledger.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.businessledger.data.local.entity.DisplaySettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DisplaySettingsDao {
    @Query("SELECT * FROM display_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<DisplaySettingsEntity?>

    @Query("SELECT * FROM display_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsDirect(): DisplaySettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: DisplaySettingsEntity)

    @Update
    suspend fun updateSettings(settings: DisplaySettingsEntity)
}
