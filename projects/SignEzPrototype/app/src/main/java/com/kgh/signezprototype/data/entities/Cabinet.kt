package com.kgh.signezprototype.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cabinets")
data class Cabinet(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var name: String,
    var cabinetWidth: Double,
    var cabinetHeight: Double,
    var moduleRowCount: Int,
    var moduleColCount: Int,
    var repImage: ByteArray
)

