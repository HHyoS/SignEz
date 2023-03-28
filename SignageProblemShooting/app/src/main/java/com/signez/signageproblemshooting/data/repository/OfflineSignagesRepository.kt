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

import com.signez.signageproblemshooting.data.dao.*
import com.signez.signageproblemshooting.data.entities.*
import kotlinx.coroutines.flow.Flow

class OfflineSignagesRepository(private val signageDao: SignageDao) : SignagesRepository {
    override fun getAllSignagesStream(): Flow<List<Signage>> = signageDao.getAllSignages()

    override fun getSignageStream(id: Long): Flow<Signage?> = signageDao.getSignage(id)

    override suspend fun insertSignage(signage: Signage) = signageDao.insert(signage)

    override suspend fun deleteSignage(signage: Signage) = signageDao.delete(signage)

    override suspend fun updateSignage(signage: Signage) = signageDao.update(signage)
    override suspend fun getSignageById(signageId: Long) = signageDao.getSignageById(signageId)
    override suspend fun getSignageList() = signageDao.getSignageList()
}
