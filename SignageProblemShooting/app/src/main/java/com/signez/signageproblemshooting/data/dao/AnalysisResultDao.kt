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
import kotlinx.coroutines.flow.Flow

/**
 * Database access object to access the Inventory database
 */
@Dao
interface AnalysisResultDao {

    @Query("SELECT * from results ORDER BY resultDate DESC")
    fun getAllResults(): Flow<List<AnalysisResult>>

    @Query("SELECT * from results WHERE id = :id")
    fun getResult(id: Long): Flow<AnalysisResult>

    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Item into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(analysisResult: AnalysisResult): Long

    @Update
    suspend fun update(analysisResult: AnalysisResult)

    @Delete
    suspend fun delete(analysisResult: AnalysisResult)

    @Query("""
        SELECT results.* FROM results
        INNER JOIN signages ON signages.id = results.signageId
        WHERE signages.id = :signageId
    """)
    suspend fun getResultsBySignageId(signageId: Long): List<AnalysisResult>

    @Query("""
        SELECT * FROM results
        WHERE results.id = :resultId
    """)
    suspend fun getResultById(resultId: Long): AnalysisResult

    @Query("DELETE FROM results WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM results ORDER BY resultDate DESC LIMIT 1")
    suspend fun getMostRecentResult(): AnalysisResult
}
