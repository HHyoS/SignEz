package com.signez.signageproblemshooting.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "cabinets"
)
data class Cabinet(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    var name: String,
    var cabinetWidth: Double,
    var cabinetHeight: Double,
    var moduleRowCount: Int,
    var moduleColCount: Int,
    var repImg: ByteArray? = null
)

