package com.signez.signageproblemshooting.dbHelper_temp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.signez.signageproblemshooting.datum_temp.Cabinet

class CabinetDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CabinetDBHelper.CREATE_TABLE_CABINET)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(CabinetDBHelper.DROP_TABLE_CABINET)
        onCreate(db)
    }

    fun addCabinet(cabinet: Cabinet): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CABINET_MODEL_NAME, cabinet.name)
            put(COLUMN_HEIGHT_CABINET_WIDTH, cabinet.cabinetWidth)
            put(COLUMN_WIDTH_CABINET_HEIGHT, cabinet.cabinetHeight)
            put(COLUMN_MODULE_ROW_CNT, cabinet.moduleRowCount)
            put(COLUMN_MODULE_COL_CNT, cabinet.moduleColCount)
            put(COLUMN_REP_IMG, cabinet.repImage)
        }
        val id = db.insert(TABLE_CABINET, null, values)
        db.close()
        return id
    }

    @SuppressLint("Range")
    fun getAllCabinets(): List<Cabinet> {
        val cabinets = mutableListOf<Cabinet>()
        val query = "SELECT * FROM $TABLE_CABINET"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndex(COLUMN_CABINET_MODEL_NAME))
                val cabinetWidth = cursor.getDouble(cursor.getColumnIndex(COLUMN_HEIGHT_CABINET_WIDTH))
                val cabinetHeight = cursor.getDouble(cursor.getColumnIndex(COLUMN_WIDTH_CABINET_HEIGHT))
                val moduleRowCount = cursor.getInt(cursor.getColumnIndex(COLUMN_MODULE_ROW_CNT))
                val moduleColCount = cursor.getInt(cursor.getColumnIndex(COLUMN_MODULE_COL_CNT))
                val repImage = cursor.getBlob(cursor.getColumnIndex(COLUMN_REP_IMG))
                val cabinet = Cabinet(id, name, cabinetWidth, cabinetHeight, moduleRowCount, moduleColCount, repImage)
                cabinets.add(cabinet)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return cabinets
    }

    fun updateCabinet(cabinet: Cabinet): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CABINET_MODEL_NAME, cabinet.name)
            put(COLUMN_HEIGHT_CABINET_WIDTH, cabinet.cabinetWidth)
            put(COLUMN_WIDTH_CABINET_HEIGHT, cabinet.cabinetHeight)
            put(COLUMN_MODULE_ROW_CNT, cabinet.moduleRowCount)
            put(COLUMN_MODULE_COL_CNT, cabinet.moduleColCount)
            put(COLUMN_REP_IMG, cabinet.repImage)
        }
        val id = db.update(
            TABLE_CABINET,
            values,
            "$COLUMN_ID=?",
            arrayOf(cabinet.id.toString())
        )
        db.close()
        return id
    }

    fun deleteCabinet(cabinet: Cabinet): Int {
        val db = this.writableDatabase
        val id = db.delete(
            TABLE_CABINET,
            "$COLUMN_ID=?",
            arrayOf(cabinet.id.toString())
        )
        db.close()
        return id
    }

    companion object {
        private const val DATABASE_NAME = "MySignEz.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_CABINET = "cabinet"
        private const val COLUMN_ID = "cabinet_id"
        private const val COLUMN_CABINET_MODEL_NAME = "name"
        private const val COLUMN_HEIGHT_CABINET_WIDTH = "cabinet_width"
        private const val COLUMN_WIDTH_CABINET_HEIGHT = "cabinet_height"
        private const val COLUMN_MODULE_ROW_CNT = "module_row_cnt"
        private const val COLUMN_MODULE_COL_CNT = "module_col_cnt"
        private const val COLUMN_REP_IMG = "rep_img"

        private const val CREATE_TABLE_CABINET =
            "CREATE TABLE $TABLE_CABINET (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY," +
                    "$COLUMN_CABINET_MODEL_NAME TEXT," +
                    "$COLUMN_HEIGHT_CABINET_WIDTH REAL," +
                    "$COLUMN_WIDTH_CABINET_HEIGHT REAL," +
                    "$COLUMN_MODULE_ROW_CNT INTEGER," +
                    "$COLUMN_MODULE_COL_CNT INTEGER," +
                    "$COLUMN_REP_IMG BLOB" +
                    ")"

        private const val DROP_TABLE_CABINET = "DROP TABLE IF EXISTS $TABLE_CABINET"
    }
}