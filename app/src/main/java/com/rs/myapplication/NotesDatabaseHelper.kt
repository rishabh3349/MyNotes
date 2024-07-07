package com.rs.myapplication

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class NotesDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "notes.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_NOTES = "notes"
        const val COLUMN_ID = "_id"
        const val COLUMN_USER_ID = "user_id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_CONTENT = "content"
    }
    override fun onCreate(db: SQLiteDatabase) {
        val createNotesTable = ("CREATE TABLE $TABLE_NOTES ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_USER_ID TEXT,"
                + "$COLUMN_TITLE TEXT,"
                + "$COLUMN_CONTENT TEXT)")
        db.execSQL(createNotesTable)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTES")
        onCreate(db)
    }
}
