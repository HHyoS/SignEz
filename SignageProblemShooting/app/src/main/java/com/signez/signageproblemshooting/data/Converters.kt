package com.signez.signageproblemshooting.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.sql.Blob
import java.sql.SQLException

class Converters {
    @TypeConverter
    fun toBlob(byteArray: ByteArray?): Blob? {
        return byteArray?.let {
            try {
                java.sql.DriverManager.getConnection("jdbc:default:connection").createBlob().apply {
                    setBytes(1, byteArray)
                }
            } catch (e: SQLException) {
                e.printStackTrace()
                null
            }
        }
    }

    @TypeConverter
    fun toByteArray(blob: Blob?): ByteArray? {
        return blob?.let {
            try {
                blob.getBytes(1, blob.length().toInt())
            } catch (e: SQLException) {
                e.printStackTrace()
                null
            }
        }
    }

}