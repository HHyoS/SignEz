package com.kgh.signezprototype

import android.app.Application
import androidx.room.Room
import com.kgh.signezprototype.data.AppContainer
import com.kgh.signezprototype.data.AppDataContainer
import com.kgh.signezprototype.data.SignEzDatabase

class SignEzApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}