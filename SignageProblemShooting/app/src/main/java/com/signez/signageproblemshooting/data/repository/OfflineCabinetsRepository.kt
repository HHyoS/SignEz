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
import com.signez.signageproblemshooting.data.dao.CabinetDao
import com.signez.signageproblemshooting.data.entities.AnalysisResult
import com.signez.signageproblemshooting.data.entities.Cabinet
import kotlinx.coroutines.flow.Flow

class OfflineCabinetsRepository(private val cabinetDao: CabinetDao) : CabinetsRepository {
    override fun getAllCabinetsStream(): Flow<List<Cabinet>> = cabinetDao.getAllCabinets()

    override fun getCabinetStream(id: Long): Flow<Cabinet?> = cabinetDao.getCabinet(id)

    override suspend fun insertCabinet(cabinet: Cabinet) = cabinetDao.insert(cabinet)

    override suspend fun deleteCabinet(cabinet: Cabinet) = cabinetDao.delete(cabinet)

    override suspend fun updateCabinet(cabinet: Cabinet) = cabinetDao.update(cabinet)
    override suspend fun getCabinetBySignageId(signageId: Long) = cabinetDao.getCabinetBySignageId(signageId)
    override suspend fun getNewCabinet(cabinetId: Long) = cabinetDao.getNewCabinet(cabinetId)
}
