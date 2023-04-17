/*
 * Copyright (C) 2022 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.signez.signageproblemshooting.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.signez.signageproblemshooting.data.entities.ErrorImage
import kotlinx.coroutines.flow.Flow

/**
 * Database access object to access the Inventory database
 */
@Dao
interface ErrorImageDao {

    @Query("SELECT * from error_images")
    fun getAllImages(): Flow<List<ErrorImage>>

    @Query("SELECT * from error_images WHERE id = :id")
    fun getImage(id: Long): Flow<ErrorImage>

    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Item into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(image: ErrorImage): Long

    @Update
    suspend fun update(image: ErrorImage)

    @Delete
    suspend fun delete(image: ErrorImage)

    @Query("""
        SELECT error_images.* FROM error_images
        INNER JOIN error_modules ON error_modules.id = error_images.error_module_id
        WHERE error_modules.id = :error_module_id
    """)
    suspend fun getImagesByModuleId(error_module_id: Long): List<ErrorImage>

    @Query("""
        SELECT * FROM error_images
        WHERE error_module_id = :error_module_id
    """)
    suspend fun getImageById(error_module_id: Long): ErrorImage
}
