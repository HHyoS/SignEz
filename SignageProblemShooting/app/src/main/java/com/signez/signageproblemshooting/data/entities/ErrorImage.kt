package com.signez.signageproblemshooting.data.entities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.*
import java.io.ByteArrayOutputStream

@Entity(tableName = "error_images",
    foreignKeys = [
        ForeignKey(
            entity = ErrorModule::class,
            parentColumns = ["id"],
            childColumns = ["error_module_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ])
data class ErrorImage(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0,
    var error_module_id: Long,
    var evidence_image: ByteArray? = null
)