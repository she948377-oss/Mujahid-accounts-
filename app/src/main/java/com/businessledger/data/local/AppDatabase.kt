package com.businessledger.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        PartyEntity::class,
        TransactionEntity::class,
        ProductEntity::class,
        CashEntryEntity::class,
        DisplaySettingsEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun partyDao(): PartyDao
    abstract fun transactionDao(): TransactionDao
    abstract fun productDao(): ProductDao
    abstract fun cashEntryDao(): CashEntryDao
    abstract fun displaySettingsDao(): DisplaySettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mujahid_business_ledger.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Initialize clean default display settings with no demo data
                            CoroutineScope(Dispatchers.IO).launch {
                                val database = getInstance(context)
                                database.displaySettingsDao().insertOrUpdate(
                                    DisplaySettingsEntity(
                                        id = 1,
                                        businessName = "Mujahid Accounts",
                                        businessPhone = "",
                                        businessAddress = "",
                                        currencySymbol = "Rs.",
                                        currencyCode = "PKR",
                                        language = "English",
                                        languageCode = "en",
                                        showBalanceInHeader = true,
                                        compactView = false,
                                        defaultPaymentMode = "Cash",
                                        isDarkMode = true
                                    )
                                )
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
