/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.signez.signageproblemshooting.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.signez.signageproblemshooting.data.dao.*
import com.signez.signageproblemshooting.data.entities.*

/**
 * Database class with a singleton INSTANCE object.
 */
@Database(
    entities = [
        AnalysisResult::class,
        ErrorModule::class,
        ErrorImage::class,
        Signage::class,
        Cabinet::class
    ],
    version = 1,
    exportSchema = false)
abstract class SignEzDatabase : RoomDatabase() {

    abstract fun resultDao(): AnalysisResultDao
    abstract fun errorModuleDao(): ErrorModuleDao
    abstract fun imageDao(): ErrorImageDao
    abstract fun signageDao(): SignageDao
    abstract fun cabinetDao(): CabinetDao

    companion object {
        @Volatile
        private var Instance: SignEzDatabase? = null

        fun getDatabase(context: Context): SignEzDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, SignEzDatabase::class.java, "signez_database")
                    /**
                     * Setting this option in your app's database builder means that Room
                     * permanently deletes all data from the tables in your database when it
                     * attempts to perform a migration with no defined migration path.
                     */
//                    .addCallback(SignEzDatabaseCallback(context))
                    .fallbackToDestructiveMigration()
                    .build()
                    .also {
                        Instance = it
                        Log.d("SignEzDatabase", "Database created")
                    }
            }
        }
    }
}