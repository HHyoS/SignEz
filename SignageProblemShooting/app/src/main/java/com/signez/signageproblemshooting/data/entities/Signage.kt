package com.signez.signageproblemshooting.data.entities

import androidx.room.*

@Entity(tableName = "signages",
    foreignKeys = [
        ForeignKey(
            entity = Cabinet::class,
            parentColumns = ["id"],
            childColumns = ["modelId"],
            onUpdate = ForeignKey.CASCADE,
        )
    ])
data class Signage(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0,
    var name: String,
    var heightCabinetNumber: Int,
    var widthCabinetNumber: Int,
    var height: Double,
    var width: Double,
    var modelId: Long,
    var repImg: ByteArray? = null
)