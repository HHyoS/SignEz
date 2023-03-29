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
import com.kgh.signezprototype.data.entities.ErrorImage
import com.kgh.signezprototype.data.entities.ErrorModule
import com.kgh.signezprototype.data.entities.Signage
import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Signage] from a given data source.
 */
interface SignagesRepository {
    /**
     * Retrieve all the items from the the given data source.
     */
    fun getAllSignagesStream(): Flow<List<Signage>>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
    fun getSignageStream(id: Long): Flow<Signage?>

    /**
     * Insert item in the data source
     */
    suspend fun insertSignage(signage: Signage)

    /**
     * Delete item from the data source
     */
    suspend fun deleteSignage(signage: Signage)

    /**
     * Update item in the data source
     */
    suspend fun updateSignage(signage: Signage)

    suspend fun getSignageById(signageId: Long) : Signage

    suspend fun getSignageList(): List<Signage>
}
