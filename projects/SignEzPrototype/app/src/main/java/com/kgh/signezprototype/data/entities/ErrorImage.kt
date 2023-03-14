package com.kgh.signezprototype.data.entities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

@Entity(tableName = "error_images")
data class ErrorImage(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var error_module_id: Long,
    var evidence_image: ByteArray
)

class BitmapTypeConverter {
    @TypeConverter
    fun fromBitmap(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    @TypeConverter
    fun toBitmap(bytes: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}