package com.kgh.signezprototype.dbHelper_temp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.kgh.signezprototype.datum_temp.AnalysisResult

class AnalysisResultDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE_ANALYSIS_RESULT)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DROP_TABLE_ANALYSIS_RESULT)
        onCreate(db)
    }

    fun addAnalysisResult(result: AnalysisResult) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_SIGNAGE_ID, result.signageId)
            put(COLUMN_RESULT_DATE, result.resultDate)
        }
        db.insert(TABLE_ANALYSIS_RESULT, null, values)
        db.close()
    }

    @SuppressLint("Range")
    fun getAnalysisResultById(id: Long): AnalysisResult? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_ANALYSIS_RESULT,
            arrayOf(COLUMN_ID, COLUMN_SIGNAGE_ID, COLUMN_RESULT_DATE),
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null, null, null, null
        )
        val result: AnalysisResult? = if (cursor.moveToFirst()) {
            AnalysisResult(
                cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getLong(cursor.getColumnIndex(COLUMN_SIGNAGE_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_RESULT_DATE))
            )
        } else {
            null
        }
        cursor.close()
        db.close()
        return result
    }

    @SuppressLint("Range")
    fun getAllAnalysisResults(): List<AnalysisResult> {
        val results = mutableListOf<AnalysisResult>()
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_ANALYSIS_RESULT,
            arrayOf(COLUMN_ID, COLUMN_SIGNAGE_ID, COLUMN_RESULT_DATE),
            null, null, null, null,
            "$COLUMN_ID DESC"
        )
        if (cursor.moveToFirst()) {
            do {
                val result = AnalysisResult(
                    cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                    cursor.getLong(cursor.getColumnIndex(COLUMN_SIGNAGE_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_RESULT_DATE))
                )
                results.add(result)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return results
    }

    fun updateAnalysisResult(result: AnalysisResult) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_SIGNAGE_ID, result.signageId)
            put(COLUMN_RESULT_DATE, result.resultDate)
        }
        db.update(TABLE_ANALYSIS_RESULT, values, "$COLUMN_ID = ?", arrayOf(result.id.toString()))
        db.close()
    }

    fun deleteAnalysisResult(result: AnalysisResult) {
        val db = this.writableDatabase
        db.delete(TABLE_ANALYSIS_RESULT, "$COLUMN_ID = ?", arrayOf(result.id.toString()))
        db.close()
    }

    companion object {
        private const val DATABASE_NAME = "MySignEz.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_ANALYSIS_RESULT = "AnalysisResult"
        private const val COLUMN_ID = "result_id"
        private const val COLUMN_SIGNAGE_ID= "signage_id"
        private const val COLUMN_RESULT_DATE = "result_date"

        private const val CREATE_TABLE_ANALYSIS_RESULT =
            "CREATE TABLE $TABLE_ANALYSIS_RESULT (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY," +
                    "$COLUMN_SIGNAGE_ID INTEGER," +
                    "$COLUMN_RESULT_DATE TEXT" +
                    ")"

        private const val DROP_TABLE_ANALYSIS_RESULT = "DROP TABLE IF EXISTS $TABLE_ANALYSIS_RESULT"
    }
}