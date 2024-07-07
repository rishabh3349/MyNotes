package com.rs.myapplication

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class NotesRepository(context: Context) {

    private val dbHelper = NotesDbHelper(context)

    fun addNote(userId: String, title: String, content: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("user_id", userId)
            put("title", title)
            put("content", content)
        }
        db.insert("notes", null, values)
    }

    fun getNotes(userId: String): List<Note> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "notes",
            arrayOf("id", "title", "content"),
            "user_id = ?",
            arrayOf(userId),
            null, null, null
        )

        val notes = mutableListOf<Note>()
        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
            val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
            val content = cursor.getString(cursor.getColumnIndexOrThrow("content"))
            notes.add(Note(id, title, content))
        }
        cursor.close()
        return notes
    }

    fun updateNote(id: Long, title: String, content: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("title", title)
            put("content", content)
        }
        db.update("notes", values, "id = ?", arrayOf(id.toString()))
    }

    fun deleteNote(id: Long) {
        val db = dbHelper.writableDatabase
        db.delete("notes", "id = ?", arrayOf(id.toString()))
    }

    private class NotesDbHelper(context: Context) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE notes (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "user_id TEXT, " +
                        "title TEXT, " +
                        "content TEXT)"
            )
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS notes")
            onCreate(db)
        }

        companion object {
            private const val DATABASE_VERSION = 1
            private const val DATABASE_NAME = "Notes.db"
        }
    }
}
