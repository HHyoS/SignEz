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

import androidx.room.Query
import com.signez.signageproblemshooting.data.entities.*
import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [ErrorModule] from a given data source.
 */
interface ErrorModulesRepository {
    /**
     * Retrieve all the items from the the given data source.
     */
    fun getAllErrorModulesStream(): Flow<List<ErrorModule>>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
    fun getErrorModuleStream(id: Long): Flow<ErrorModule?>

    /**
     * Insert item in the data source
     */
    suspend fun insertErrorModule(module: ErrorModule): Long

    /**
     * Delete item from the data source
     */
    suspend fun deleteErrorModule(module: ErrorModule)

    /**
     * Update item in the data source
     */
    suspend fun updateErrorModule(module: ErrorModule)

    suspend fun getModulesByResultId(resultId: Long): List<ErrorModule>

    suspend fun getModuleById(resultId: Long): ErrorModule

    suspend fun getModulesByXYResultId(x: Int, y: Int, resultId: Long): List<ErrorModuleWithImage>
}
