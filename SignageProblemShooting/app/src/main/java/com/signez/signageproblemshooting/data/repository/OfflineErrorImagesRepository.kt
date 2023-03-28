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

import com.signez.signageproblemshooting.data.dao.ErrorImageDao
import com.signez.signageproblemshooting.data.entities.ErrorImage
import kotlinx.coroutines.flow.Flow

class OfflineErrorImagesRepository(private val imageDao: ErrorImageDao) : ErrorImagesRepository {
    override fun getAllImagesStream(): Flow<List<ErrorImage>> = imageDao.getAllImages()

    override fun getImageStream(id: Long): Flow<ErrorImage?> = imageDao.getImage(id)

    override suspend fun insertImage(image: ErrorImage) = imageDao.insert(image)

    override suspend fun deleteImage(image: ErrorImage) = imageDao.delete(image)

    override suspend fun updateImage(image: ErrorImage) = imageDao.update(image)

    override suspend fun getImageByModuleId(error_module_id: Long) = imageDao.getImageByModuleId(error_module_id)
}
