package com.signez.signageproblemshooting.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "error_modules",
    foreignKeys = [
        ForeignKey(
            entity = AnalysisResult::class,
            parentColumns = ["id"],
            childColumns = ["resultId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ])
data class ErrorModule(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    val resultId: Long,
    val score: Double,
    val x: Int,
    val y: Int
)