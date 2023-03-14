package com.kgh.signezprototype

import android.app.Application
import com.kgh.signezprototype.data.AppContainer
import com.kgh.signezprototype.data.AppDataContainer

class SignEzApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}