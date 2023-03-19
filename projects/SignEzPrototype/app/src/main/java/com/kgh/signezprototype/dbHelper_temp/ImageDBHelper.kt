package com.kgh.signezprototype.dbHelper_temp
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.kgh.signezprototype.datum_temp.Image
import java.io.ByteArrayOutputStream

class ImageDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_IMAGE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(DROP_TABLE_IMAGE)
        onCreate(db)
    }

    fun addImage(image: Image): Long {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(COLUMN_ERROR_MODULE_ID, image.error_module_id)
        values.put(COLUMN_EVIDENCE_IMAGE, getBitmapAsByteArray(image.evidence_image))
        val id = db.insert(TABLE_IMAGE, null, values)
        db.close()

        return id
    }

    @SuppressLint("Range")
    fun getImage(id: Long): Image? {
        val db = this.readableDatabase

        val cursor = db.query(
            TABLE_IMAGE,
            arrayOf(COLUMN_ID, COLUMN_ERROR_MODULE_ID, COLUMN_EVIDENCE_IMAGE),
            "$COLUMN_ID=?",
            arrayOf(id.toString()),
            null,
            null,
            null,
            null
        )

        return if (cursor != null && cursor.moveToFirst()) {
            val byteArray = cursor.getBlob(cursor.getColumnIndex(COLUMN_EVIDENCE_IMAGE))
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

            val image = Image(
                cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getLong(cursor.getColumnIndex(COLUMN_ERROR_MODULE_ID)),
                bitmap
            )

            cursor.close()
            image
        } else {
            cursor?.close()
            null
        }
    }

    @SuppressLint("Range")
    fun getAllImages(): List<Image> {
        val images = ArrayList<Image>()

        val selectQuery = "SELECT  * FROM $TABLE_IMAGE ORDER BY $COLUMN_ID DESC"
        val db = this.readableDatabase
        val cursor: Cursor?

        cursor = db.rawQuery(selectQuery, null)

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val byteArray = cursor.getBlob(cursor.getColumnIndex(COLUMN_EVIDENCE_IMAGE))
                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

                val image = Image(
                    cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                    cursor.getLong(cursor.getColumnIndex(COLUMN_ERROR_MODULE_ID)),
                    bitmap
                )
                images.add(image)
            } while (cursor.moveToNext())

            cursor.close()
        }

        return images
    }

    fun updateImage(image: Image): Int {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(COLUMN_ERROR_MODULE_ID, image.error_module_id)
        values.put(COLUMN_EVIDENCE_IMAGE, getBitmapAsByteArray(image.evidence_image))

        return db.update(
            TABLE_IMAGE,
            values,
            "$COLUMN_ID = ?",
            arrayOf(image.id.toString())
        )
    }

    fun deleteImage(id: Long) {
        val db = this.writableDatabase
        db.delete(
            TABLE_IMAGE,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
        db.close()
    }

    private fun getBitmapAsByteArray(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream)
        return outputStream.toByteArray()
    }

    private fun getByteArrayAsBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    companion object {
        private const val DATABASE_NAME = "MySignEz.db"
        private const val DATABASE_VERSION = 1


        private const val TABLE_IMAGE = "Image"
        private const val COLUMN_ID = "image_id"
        private const val COLUMN_ERROR_MODULE_ID = "error_module_id"
        private const val COLUMN_EVIDENCE_IMAGE = "evidence_image"

        private const val CREATE_TABLE_IMAGE =
            "CREATE TABLE $TABLE_IMAGE (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY," +
                    "$COLUMN_ERROR_MODULE_ID INTEGER," +
                    "$COLUMN_EVIDENCE_IMAGE BLOB" +
                    ")"

        private const val DROP_TABLE_IMAGE = "DROP TABLE IF EXISTS $TABLE_IMAGE"
    }

}