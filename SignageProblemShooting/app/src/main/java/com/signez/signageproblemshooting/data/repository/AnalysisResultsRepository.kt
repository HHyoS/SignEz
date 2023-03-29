/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.signez.signageproblemshooting.data.repository

import com.signez.signageproblemshooting.data.entities.AnalysisResult
import com.signez.signageproblemshooting.data.entities.Signage
import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [AnalysisResult] from a given data source.
 */
interface AnalysisResultsRepository {
    /**
     * Retrieve all the items from the the given data source.
     */
    fun getAllResultsStream(): Flow<List<AnalysisResult>>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
    fun getResultStream(id: Long): Flow<AnalysisResult?>

    /**
     * Insert item in the data source
     */
    suspend fun insertResult(analysisResult: AnalysisResult): Long

    /**
     * Delete item from the data source
     */
    suspend fun deleteResult(analysisResult: AnalysisResult)

    /**
     * Update item in the data source
     */
    suspend fun updateResult(analysisResult: AnalysisResult)
    suspend fun getResultsBySignageId(signageId: Long) : List<AnalysisResult>
    suspend fun getResultById(resultId: Long): AnalysisResult

    suspend fun deleteById(id: Long)
}
