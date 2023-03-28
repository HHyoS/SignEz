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

import com.signez.signageproblemshooting.data.dao.AnalysisResultDao
import com.signez.signageproblemshooting.data.entities.AnalysisResult
import kotlinx.coroutines.flow.Flow

class OfflineAnalysisResultsRepository(private val analysisResultDao: AnalysisResultDao) : AnalysisResultsRepository {
    override fun getAllResultsStream(): Flow<List<AnalysisResult>> = analysisResultDao.getAllResults()

    override fun getResultStream(id: Long): Flow<AnalysisResult?> = analysisResultDao.getResult(id)

    override suspend fun insertResult(analysisResult: AnalysisResult) = analysisResultDao.insert(analysisResult)

    override suspend fun deleteResult(analysisResult: AnalysisResult) = analysisResultDao.delete(analysisResult)

    override suspend fun updateResult(analysisResult: AnalysisResult) = analysisResultDao.update(analysisResult)
}
