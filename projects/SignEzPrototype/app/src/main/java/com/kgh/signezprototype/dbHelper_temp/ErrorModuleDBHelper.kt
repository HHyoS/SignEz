package com.kgh.signezprototype.dbHelper_temp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.kgh.signezprototype.datum_temp.ErrorModule

class ErrorModuleDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_ANALYSIS_RESULT)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(DROP_TABLE_ERROR_MODULE)
        onCreate(db)
    }

    fun addErrorModule(errorModule: ErrorModule): Long {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(COLUMN_RESULT_ID, errorModule.resultId)
        values.put(COLUMN_SCORE, errorModule.score)
        values.put(COLUMN_X, errorModule.x)
        values.put(COLUMN_Y, errorModule.y)

        val id = db.insert(TABLE_ERROR_MODULE, null, values)
        db.close()

        return id
    }

    @SuppressLint("Range")
    fun getErrorModule(id: Long): ErrorModule? {
        val db = this.readableDatabase

        val cursor = db.query(
            TABLE_ERROR_MODULE,
            arrayOf(COLUMN_ID, COLUMN_RESULT_ID, COLUMN_SCORE, COLUMN_X, COLUMN_Y),
            "$COLUMN_ID=?",
            arrayOf(id.toString()),
            null,
            null,
            null,
            null
        )

        return if (cursor != null && cursor.moveToFirst()) {
            val errorModule = ErrorModule(
                cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getLong(cursor.getColumnIndex(COLUMN_RESULT_ID)),
                cursor.getDouble(cursor.getColumnIndex(COLUMN_SCORE)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_X)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_Y))
            )
            cursor.close()
            errorModule
        } else {
            cursor?.close()
            null
        }
    }

    @SuppressLint("Range")
    fun getAllErrorModules(): List<ErrorModule> {
        val errorModules = ArrayList<ErrorModule>()

        val selectQuery = "SELECT  * FROM $TABLE_ERROR_MODULE ORDER BY $COLUMN_ID DESC"
        val db = this.readableDatabase
        val cursor: Cursor?

        cursor = db.rawQuery(selectQuery, null)

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val errorModule = ErrorModule(
                    cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                    cursor.getLong(cursor.getColumnIndex(COLUMN_RESULT_ID)),
                    cursor.getDouble(cursor.getColumnIndex(COLUMN_SCORE)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_X)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_Y))
                )
                errorModules.add(errorModule)
            } while (cursor.moveToNext())

            cursor.close()
        }

        return errorModules
    }

    fun updateErrorModule(errorModule: ErrorModule): Int {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(COLUMN_RESULT_ID, errorModule.resultId)
        values.put(COLUMN_SCORE, errorModule.score)
        values.put(COLUMN_X, errorModule.x)
        values.put(COLUMN_Y, errorModule.y)

        return db.update(
            TABLE_ERROR_MODULE,
            values,
            "$COLUMN_ID = ?",
            arrayOf(errorModule.id.toString())
        )
    }

    fun deleteErrorModule(id: Long) {
        val db = this.writableDatabase
        db.delete(
            TABLE_ERROR_MODULE,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
        db.close()
    }

    companion object {
        private const val DATABASE_NAME = "MySignEz.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_ERROR_MODULE = "ErrorModule"
        private const val COLUMN_ID = "error_module_id"
        private const val COLUMN_RESULT_ID = "result_id"
        private const val COLUMN_SCORE= "score"
        private const val COLUMN_X = "x"
        private const val COLUMN_Y = "y"

        private const val CREATE_TABLE_ANALYSIS_RESULT =
            "CREATE TABLE $TABLE_ERROR_MODULE (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY," +
                    "$COLUMN_RESULT_ID INTEGER," +
                    "$COLUMN_SCORE REAL," +
                    "$COLUMN_X REAL," +
                    "$COLUMN_Y REAL" +
                    ")"

        private const val DROP_TABLE_ERROR_MODULE = "DROP TABLE IF EXISTS $TABLE_ERROR_MODULE"
    }
}