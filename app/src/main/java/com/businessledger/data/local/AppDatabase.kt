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
    version = 1,
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
                            // Populate default display settings & helpful starter sample data
                            CoroutineScope(Dispatchers.IO).launch {
                                val database = getInstance(context)
                                database.displaySettingsDao().insertOrUpdate(
                                    DisplaySettingsEntity(
                                        id = 1,
                                        businessName = "Mujahid Accounts",
                                        businessPhone = "+92 300 1234567",
                                        businessAddress = "Main Bazaar, Commercial Market",
                                        currencySymbol = "Rs.",
                                        language = "Bilingual",
                                        showBalanceInHeader = true,
                                        compactView = false,
                                        defaultPaymentMode = "Cash",
                                        isDarkMode = false
                                    )
                                )
                                // Add starter parties
                                val p1Id = database.partyDao().insertParty(
                                    PartyEntity(
                                        name = "Ali Traders",
                                        phone = "0300-9876543",
                                        address = "Shop # 12, Grain Market",
                                        partyType = "CUSTOMER",
                                        openingBalance = 5000.0,
                                        currentBalance = 15000.0,
                                        notes = "Wholesale buyer",
                                        createdAt = System.currentTimeMillis() - 86400000L * 5,
                                        updatedAt = System.currentTimeMillis() - 86400000L * 1
                                    )
                                )
                                val p2Id = database.partyDao().insertParty(
                                    PartyEntity(
                                        name = "Tariq Goods & Co",
                                        phone = "0321-4567890",
                                        address = "Industrial Area, Gate 2",
                                        partyType = "SUPPLIER",
                                        openingBalance = 0.0,
                                        currentBalance = -8500.0,
                                        notes = "Packaging supplier",
                                        createdAt = System.currentTimeMillis() - 86400000L * 7,
                                        updatedAt = System.currentTimeMillis() - 86400000L * 2
                                    )
                                )
                                val p3Id = database.partyDao().insertParty(
                                    PartyEntity(
                                        name = "Chaudhry Autos",
                                        phone = "0333-1122334",
                                        address = "Circular Road",
                                        partyType = "CUSTOMER",
                                        openingBalance = 2000.0,
                                        currentBalance = 4500.0,
                                        notes = "Regular customer",
                                        createdAt = System.currentTimeMillis() - 86400000L * 10,
                                        updatedAt = System.currentTimeMillis() - 86400000L * 1
                                    )
                                )

                                // Add initial transactions for parties
                                if (p1Id > 0) {
                                    database.transactionDao().insertTransaction(
                                        TransactionEntity(
                                            partyId = p1Id,
                                            amount = 12000.0,
                                            type = "GAVE",
                                            date = System.currentTimeMillis() - 86400000L * 3,
                                            description = "Delivered Batch # 42",
                                            invoiceNumber = "INV-1001",
                                            paymentMethod = "Credit/Udhaar",
                                            billImagePath = null,
                                            createdAt = System.currentTimeMillis() - 86400000L * 3
                                        )
                                    )
                                    database.transactionDao().insertTransaction(
                                        TransactionEntity(
                                            partyId = p1Id,
                                            amount = 2000.0,
                                            type = "GOT",
                                            date = System.currentTimeMillis() - 86400000L * 1,
                                            description = "Cash partial payment received",
                                            invoiceNumber = "REC-201",
                                            paymentMethod = "Cash",
                                            billImagePath = null,
                                            createdAt = System.currentTimeMillis() - 86400000L * 1
                                        )
                                    )
                                }

                                if (p2Id > 0) {
                                    database.transactionDao().insertTransaction(
                                        TransactionEntity(
                                            partyId = p2Id,
                                            amount = 8500.0,
                                            type = "GOT",
                                            date = System.currentTimeMillis() - 86400000L * 2,
                                            description = "Raw material carton supplies bill",
                                            invoiceNumber = "SUP-889",
                                            paymentMethod = "Credit/Udhaar",
                                            billImagePath = null,
                                            createdAt = System.currentTimeMillis() - 86400000L * 2
                                        )
                                    )
                                }

                                // Add starter inventory products
                                database.productDao().insertProduct(
                                    ProductEntity(
                                        name = "Super Basmati Rice 25kg",
                                        sku = "SBR-25",
                                        purchasePrice = 6200.0,
                                        sellingPrice = 7500.0,
                                        stockQuantity = 45.0,
                                        unit = "Bags",
                                        category = "Food & Grains",
                                        minStockAlert = 10.0,
                                        updatedAt = System.currentTimeMillis()
                                    )
                                )
                                database.productDao().insertProduct(
                                    ProductEntity(
                                        name = "Cooking Oil Tin 16L",
                                        sku = "COT-16",
                                        purchasePrice = 8100.0,
                                        sellingPrice = 8900.0,
                                        stockQuantity = 4.0, // Low stock alert demo
                                        unit = "Tins",
                                        category = "Oils",
                                        minStockAlert = 8.0,
                                        updatedAt = System.currentTimeMillis()
                                    )
                                )
                                database.productDao().insertProduct(
                                    ProductEntity(
                                        name = "Refined White Sugar 50kg",
                                        sku = "RWS-50",
                                        purchasePrice = 6400.0,
                                        sellingPrice = 7000.0,
                                        stockQuantity = 22.0,
                                        unit = "Bags",
                                        category = "Food & Grains",
                                        minStockAlert = 5.0,
                                        updatedAt = System.currentTimeMillis()
                                    )
                                )

                                // Add starter cash entries
                                database.cashEntryDao().insertCashEntry(
                                    CashEntryEntity(
                                        type = "CASH_IN",
                                        amount = 15000.0,
                                        category = "Counter Sales",
                                        description = "Morning counter cash sales",
                                        paymentMode = "Cash",
                                        date = System.currentTimeMillis() - 3600000L * 3,
                                        createdAt = System.currentTimeMillis() - 3600000L * 3
                                    )
                                )
                                database.cashEntryDao().insertCashEntry(
                                    CashEntryEntity(
                                        type = "CASH_OUT",
                                        amount = 1200.0,
                                        category = "Shop Expenses",
                                        description = "Tea, electricity & courier bills",
                                        paymentMode = "Cash",
                                        date = System.currentTimeMillis() - 3600000L * 1,
                                        createdAt = System.currentTimeMillis() - 3600000L * 1
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
