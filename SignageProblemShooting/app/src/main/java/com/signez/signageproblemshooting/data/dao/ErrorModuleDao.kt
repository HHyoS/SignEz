/*
 * Copyright (C) 2022 The Android Open Source Project.
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

package com.signez.signageproblemshooting.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.signez.signageproblemshooting.data.entities.AnalysisResult
import com.signez.signageproblemshooting.data.entities.ErrorImage
import com.signez.signageproblemshooting.data.entities.ErrorModule
import com.signez.signageproblemshooting.data.entities.Item
import kotlinx.coroutines.flow.Flow

/**
 * Database access object to access the Inventory database
 */
@Dao
interface ErrorModuleDao {

    @Query("SELECT * from error_modules")
    fun getAllErrorModules(): Flow<List<ErrorModule>>

    @Query("SELECT * from error_modules WHERE id = :id")
    fun getErrorModule(id: Long): Flow<ErrorModule>

    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Item into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(errorModule: ErrorModule)

    @Update
    suspend fun update(errorModule: ErrorModule)

    @Delete
    suspend fun delete(errorModule: ErrorModule)

    @Query("""
        SELECT error_modules.* FROM error_modules
        INNER JOIN results ON results.id = error_modules.resultId
        WHERE results.id = :resultId
    """)
    suspend fun getModulesByResultId(resultId: Long): List<ErrorModule>

    @Query("""
        SELECT * FROM error_modules
        WHERE resultId = :resultId
    """)
    suspend fun getModuleById(resultId: Long): ErrorModule

}
