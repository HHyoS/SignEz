package com.kgh.signezprototype.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "signages")
data class Signage(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var name: String,
    var heightCabinetNumber: Int,
    var widthCabinetNumber: Int,
    var height: Double,
    var width: Double,
    var modelId: Long
)