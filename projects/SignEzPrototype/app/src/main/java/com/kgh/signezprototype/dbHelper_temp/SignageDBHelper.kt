package com.kgh.signezprototype.dbHelper_temp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.kgh.signezprototype.datum_temp.Signage

class SignageDBHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE_SIGNAGE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DROP_TABLE_SIGNAGE)
        onCreate(db)
    }

    fun addSignage(signage: Signage): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, signage.name)
            put(COLUMN_HEIGHT_CABINET_NUMBER, signage.heightCabinetNumber)
            put(COLUMN_WIDTH_CABINET_NUMBER, signage.widthCabinetNumber)
            put(COLUMN_HEIGHT, signage.height)
            put(COLUMN_WIDTH, signage.width)
            put(COLUMN_MODEL_ID, signage.modelId)
        }
        val id = db.insert(TABLE_SIGNAGE, null, values)
        db.close()
        return id
    }

    @SuppressLint("Range")
    fun getSignage(id: Int): Signage? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_SIGNAGE,
            arrayOf(
                COLUMN_ID,
                COLUMN_NAME,
                COLUMN_HEIGHT_CABINET_NUMBER,
                COLUMN_WIDTH_CABINET_NUMBER,
                COLUMN_HEIGHT,
                COLUMN_WIDTH,
                COLUMN_MODEL_ID
            ),
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        var signage: Signage? = null
        if (cursor.moveToFirst()) {
            signage = Signage(
                cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_HEIGHT_CABINET_NUMBER)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_WIDTH_CABINET_NUMBER)),
                cursor.getDouble(cursor.getColumnIndex(COLUMN_HEIGHT)),
                cursor.getDouble(cursor.getColumnIndex(COLUMN_WIDTH)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_MODEL_ID))
            )
        }
        cursor.close()
        db.close()
        return signage
    }

    @SuppressLint("Range")
    fun getAllSignage(): List<Signage> {
        val signageList = mutableListOf<Signage>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_SIGNAGE", null)
        if (cursor.moveToFirst()) {
            do {
                val signage = Signage(
                    cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_HEIGHT_CABINET_NUMBER)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_WIDTH_CABINET_NUMBER)),
                    cursor.getDouble(cursor.getColumnIndex(COLUMN_HEIGHT)),
                    cursor.getDouble(cursor.getColumnIndex(COLUMN_WIDTH)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_MODEL_ID))
                )
                signageList.add(signage)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return signageList
    }

    fun updateSignage(signage: Signage): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, signage.name)
            put(COLUMN_HEIGHT_CABINET_NUMBER, signage.heightCabinetNumber)
            put(COLUMN_WIDTH_CABINET_NUMBER, signage.widthCabinetNumber)
            put(COLUMN_HEIGHT, signage.height)
            put(COLUMN_WIDTH, signage.width)
            put(COLUMN_MODEL_ID, signage.modelId)
        }
        val rows = db.update(
            TABLE_SIGNAGE,
            values,
            "$COLUMN_ID = ?",
            arrayOf(signage.id.toString())
        )
        db.close()
        return rows
    }
    fun deleteSignage(id: Int): Int {
        val db = this.writableDatabase
        val rows = db.delete(
            TABLE_SIGNAGE,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
        db.close()
        return rows
    }

    companion object {
        private const val DATABASE_NAME = "MySignEz.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_SIGNAGE = "signage"
        private const val COLUMN_ID = "signage_id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_HEIGHT_CABINET_NUMBER = "height_cabinet_number"
        private const val COLUMN_WIDTH_CABINET_NUMBER = "width_cabinet_number"
        private const val COLUMN_HEIGHT = "height"
        private const val COLUMN_WIDTH = "width"
        private const val COLUMN_MODEL_ID = "model_id"

        private const val CREATE_TABLE_SIGNAGE =
            "CREATE TABLE $TABLE_SIGNAGE (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY," +
                    "$COLUMN_NAME TEXT," +
                    "$COLUMN_HEIGHT_CABINET_NUMBER INTEGER," +
                    "$COLUMN_WIDTH_CABINET_NUMBER INTEGER," +
                    "$COLUMN_HEIGHT REAL," +
                    "$COLUMN_WIDTH REAL," +
                    "$COLUMN_MODEL_ID INTEGER" +
                    ")"

        private const val DROP_TABLE_SIGNAGE = "DROP TABLE IF EXISTS $TABLE_SIGNAGE"
    }
}


