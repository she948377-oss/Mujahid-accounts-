package com.businessledger

import android.app.Application
import com.businessledger.di.AppModule

class BusinessLedgerApp : Application() {
    lateinit var appModule: AppModule
        private set

    override fun onCreate() {
        super.onCreate()
        appModule = AppModule(this)
    }
}
