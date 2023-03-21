package com.kgh.signezprototype.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.kgh.signezprototype.data.Converters
import java.sql.Blob

@Entity(tableName = "signages")
@TypeConverters(Converters::class)
data class Signage(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var name: String,
    var heightCabinetNumber: Int,
    var widthCabinetNumber: Int,
    var height: Double,
    var width: Double,
    var modelId: Long,
    var repImg: ByteArray? = null
)