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
import com.signez.signageproblemshooting.data.entities.Cabinet
import com.signez.signageproblemshooting.data.entities.Item
import com.signez.signageproblemshooting.data.entities.Signage
import kotlinx.coroutines.flow.Flow

/**
 * Database access object to access the Inventory database
 */
@Dao
interface SignageDao {

    @Query(" SELECT * FROM signages ORDER BY name ASC")
    fun getAllSignages(): Flow<List<Signage>>

    @Query("SELECT * from signages WHERE id = :id")
    fun getSignage(id: Long): Flow<Signage>

    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Item into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(signage: Signage): Long

    @Update
    suspend fun update(signage: Signage)

    @Delete
    suspend fun delete(signage: Signage)

    @Query("""
        SELECT * FROM signages
        WHERE signages.id = :signageId
    """)
    suspend fun getSignageById(signageId: Long): Signage

    @Query("SELECT * from signages ORDER BY name ASC")
    fun getSignageList(): List<Signage>
}
