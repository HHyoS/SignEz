package com.signez.signageproblemshooting.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

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
    @ColumnInfo(name = "id")
    var id: Long = 0,
    var signageId: Long = 0,
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    var resultDate: String = getCurrentDateTime()
)
{
    companion object {
        private fun getCurrentDateTime(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            return sdf.format(Date())
        }
    }
}
