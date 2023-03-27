package com.signez.signageproblemshooting.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "results",
    foreignKeys = [
        ForeignKey(
            entity = Signage::class,
            parentColumns = ["id"],
            childColumns = ["signageId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ])
data class AnalysisResult(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var signageId: Long = 0,
    var resultDate: String = ""
)