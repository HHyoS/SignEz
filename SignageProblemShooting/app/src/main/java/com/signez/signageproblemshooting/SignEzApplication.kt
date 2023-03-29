package com.signez.signageproblemshooting

import android.app.Application
import androidx.room.Room
import com.signez.signageproblemshooting.data.AppContainer
import com.signez.signageproblemshooting.data.AppDataContainer
import com.signez.signageproblemshooting.data.SignEzDatabase
import com.signez.signageproblemshooting.ui.analysis.AnalysisViewModel

class SignEzApplication : Application() {
    lateinit var container: AppContainer
    companion object {
        lateinit var instance: SignEzApplication
            private set
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
        container = AppDataContainer(this)
    }
}