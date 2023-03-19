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

package com.kgh.signezprototype.data.repository

import com.kgh.signezprototype.data.dao.AnalysisResultDao
import com.kgh.signezprototype.data.dao.CabinetDao
import com.kgh.signezprototype.data.dao.ErrorImageDao
import com.kgh.signezprototype.data.dao.ErrorModuleDao
import com.kgh.signezprototype.data.entities.AnalysisResult
import com.kgh.signezprototype.data.entities.Cabinet
import com.kgh.signezprototype.data.entities.ErrorImage
import com.kgh.signezprototype.data.entities.ErrorModule
import kotlinx.coroutines.flow.Flow

class OfflineErrorModulesRepository(private val moduleDao: ErrorModuleDao) : ErrorModulesRepository {
    override fun getAllErrorModulesStream(): Flow<List<ErrorModule>> = moduleDao.getAllErrorModules()

    override fun getErrorModuleStream(id: Long): Flow<ErrorModule?> = moduleDao.getErrorModule(id)

    override suspend fun insertErrorModule(module: ErrorModule) = moduleDao.insert(module)

    override suspend fun deleteErrorModule(module: ErrorModule) = moduleDao.delete(module)

    override suspend fun updateErrorModule(module: ErrorModule) = moduleDao.update(module)
}
