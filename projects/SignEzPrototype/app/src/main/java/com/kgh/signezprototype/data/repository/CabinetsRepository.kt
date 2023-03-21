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

import com.kgh.signezprototype.data.entities.Cabinet
import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Cabinet] from a given data source.
 */
interface CabinetsRepository {
    /**
     * Retrieve all the items from the the given data source.
     */
    fun getAllCabinetsStream(): Flow<List<Cabinet>>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
    fun getCabinetStream(id: Long): Flow<Cabinet?>

    /**
     * Insert item in the data source
     */
    suspend fun insertCabinet(cabinet: Cabinet)

    /**
     * Delete item from the data source
     */
    suspend fun deleteCabinet(cabinet: Cabinet)

    /**
     * Update item in the data source
     */
    suspend fun updateCabinet(cabinet: Cabinet)

    suspend fun getCabinetBySignageId(signageId: Long): Cabinet
}
