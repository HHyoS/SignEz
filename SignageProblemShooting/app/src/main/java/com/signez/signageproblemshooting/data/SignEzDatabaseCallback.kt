package com.signez.signageproblemshooting.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.signez.signageproblemshooting.R
import java.io.ByteArrayOutputStream

class SignEzDatabaseCallback (private val context: Context) : RoomDatabase.Callback() {
    var isInitialDataInserted = false;
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        // Insert initial data into the database
        db.insert("results", SQLiteDatabase.CONFLICT_REPLACE, ContentValues().apply {
            put("id", 1L)
            put("signageId",6L)
        })
        db.insert("error_modules", SQLiteDatabase.CONFLICT_REPLACE, ContentValues().apply {
            put("id", 1L)
            put("resultId",1L)
            put("score",0.871612787246704)
            put("x",17)
            put("y",40)
        })
        db.insert("error_modules", SQLiteDatabase.CONFLICT_REPLACE, ContentValues().apply {
            put("id", 2L)
            put("resultId",1L)
            put("score",0.73947936296463)
            put("x",18)
            put("y",40)
        })
        db.insert("error_modules", SQLiteDatabase.CONFLICT_REPLACE, ContentValues().apply {
            put("id", 3L)
            put("resultId",1L)
            put("score",0.760273933410644)
            put("x",19)
            put("y",40)
        })
        db.insert("error_modules", SQLiteDatabase.CONFLICT_REPLACE, ContentValues().apply {
            put("id", 4L)
            put("resultId",1L)
            put("score",0.846648931503295)
            put("x",20)
            put("y",40)
        })
        db.insert("error_modules", SQLiteDatabase.CONFLICT_REPLACE, ContentValues().apply {
            put("id", 5L)
            put("resultId",1L)
            put("score",0.784361183643341)
            put("x",22)
            put("y",40)
        })
        db.insert("error_modules", SQLiteDatabase.CONFLICT_REPLACE, ContentValues().apply {
            put("id", 6L)
            put("resultId",1L)
            put("score",0.840295851230621)
            put("x",24)
            put("y",40)
        })
        db.insert("error_modules", SQLiteDatabase.CONFLICT_REPLACE, ContentValues().apply {
            put("id", 7L)
            put("resultId",1L)
            put("score",0.840969502925872)
            put("x",26)
            put("y",40)
        })
        db.insert("error_modules", SQLiteDatabase.CONFLICT_REPLACE, ContentValues().apply {
            put("id", 8L)
            put("resultId",1L)
            put("score",0.831808984279632)
            put("x",29)
            put("y",40)
        })
        db.insert("error_modules", SQLiteDatabase.CONFLICT_REPLACE, ContentValues().apply {
            put("id", 9L)
            put("resultId",1L)
            put("score",0.766186773777008)
            put("x",30)
            put("y",40)
        })
        db.insert("error_modules", SQLiteDatabase.CONFLICT_REPLACE, ContentValues().apply {
            put("id", 10L)
            put("resultId",1L)
            put("score",0.74423861503601)
            put("x",31)
            put("y",40)
        })

        db.insert("error_images", SQLiteDatabase.CONFLICT_REPLACE, ContentValues().apply {
            put("id", 1L)
            put("error_module_id",1L)
            put("evidence_image", imageToByteArray(context,R.drawable.image_1))
        })
        db.insert("error_images", SQLiteDatabase.CONFLICT_REPLACE, ContentValues().apply {
            put("id", 2L)
            put("error_module_id",2L)
            put("evidence_image", imageToByteArray(context,R.drawable.image_2))
        })
        db.insert("error_images", SQLiteDatabase.CONFLICT_REPLACE, ContentValues().apply {
            put("id", 3L)
            put("error_module_id",3L)
            put("evidence_image", imageToByteArray(context,R.drawable.image_3))
        })
        db.insert("error_images", SQLiteDatabase.CONFLICT_REPLACE, ContentValues().apply {
            put("id", 4L)
            put("error_module_id",4L)
            put("evidence_image", imageToByteArray(context,R.drawable.image_4))
        })
        db.insert("error_images", SQLiteDatabase.CONFLICT_REPLACE, ContentValues().apply {
            put("id", 5L)
            put("error_module_id",5L)
            put("evidence_image", imageToByteArray(context,R.drawable.image_5))
        })
        db.insert("error_images", SQLiteDatabase.CONFLICT_REPLACE, ContentValues().apply {
            put("id", 6L)
            put("error_module_id",6L)
            put("evidence_image", imageToByteArray(context,R.drawable.image_6))
        })
        db.insert("error_images", SQLiteDatabase.CONFLICT_REPLACE, ContentValues().apply {
            put("id", 7L)
            put("error_module_id",7L)
            put("evidence_image", imageToByteArray(context,R.drawable.image_7))
        })
        db.insert("error_images", SQLiteDatabase.CONFLICT_REPLACE, ContentValues().apply {
            put("id", 8L)
            put("error_module_id",8L)
            put("evidence_image", imageToByteArray(context,R.drawable.image_8))
        })
        db.insert("error_images", SQLiteDatabase.CONFLICT_REPLACE, ContentValues().apply {
            put("id", 9L)
            put("error_module_id",9L)
            put("evidence_image", imageToByteArray(context,R.drawable.image_9))
        })
        db.insert("error_images", SQLiteDatabase.CONFLICT_REPLACE, ContentValues().apply {
            put("id", 10L)
            put("error_module_id",10L)
            put("evidence_image", imageToByteArray(context,R.drawable.image_10))

        })
        isInitialDataInserted = true;
    }
}

fun String.hexStringToByteArray(): ByteArray {
    val len = length
    val data = ByteArray(len / 2)
    var i = 0
    while (i < len) {
        data[i / 2] =
            ((Character.digit(this[i], 16) shl 4) + Character.digit(this[i + 1], 16)).toByte()
        i += 2
    }
    return data
}

fun imageToByteArray(context: Context, rId: Int): ByteArray {
    val drawable = ContextCompat.getDrawable(context, rId)
    // Convert drawable to Bitmap
    val bitmap = if (drawable is BitmapDrawable) {
        drawable.bitmap
    } else {
        val bmp = drawable?.let { Bitmap.createBitmap(it.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888) }
        val canvas = bmp?.let { Canvas(it) }
        if (canvas != null) {
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
        }
        bmp
    }
    val outputStream = ByteArrayOutputStream()
    bitmap?.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
    return outputStream.toByteArray()
}