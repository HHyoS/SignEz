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

import com.signez.signageproblemshooting.data.entities.Cabinet
import com.signez.signageproblemshooting.data.entities.ErrorImage
import com.signez.signageproblemshooting.data.entities.ErrorModule
import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Image] from a given data source.
 */
interface ErrorImagesRepository {
    /**
     * Retrieve all the items from the the given data source.
     */
    fun getAllImagesStream(): Flow<List<ErrorImage>>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
    fun getImageStream(id: Long): Flow<ErrorImage?>

    /**
     * Insert item in the data source
     */
    suspend fun insertImage(image: ErrorImage): Long

    /**
     * Delete item from the data source
     */
    suspend fun deleteImage(image: ErrorImage)

    /**
     * Update item in the data source
     */
    suspend fun updateImage(image: ErrorImage)

    suspend fun getImagesByModuleId(error_module_id: Long): List<ErrorImage>

    suspend fun getImageById(error_module_id: Long): ErrorImage
}
