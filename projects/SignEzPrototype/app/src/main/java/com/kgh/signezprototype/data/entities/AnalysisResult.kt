package com.kgh.signezprototype.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "results")
data class AnalysisResult(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var signageId: Long = 0,
    var resultDate: String = ""
)