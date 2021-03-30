package com.adib.capturenote

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "Notes"

        private const val TABLE_FOLDER = "FolderTable"
        private const val FOLDER_ID = "id"
        private const val FOLDER_NAME = "name"

        private const val TABLE_NOTE = "NoteTable"
        private const val NOTE_ID = "id"
        private const val NOTE_TITLE = "title"
        private const val NOTE_CONTENT = "content"
        private const val NOTE_CREATED = "created"
        private const val NOTE_UPDATED = "updated"
        private const val HAS_IMAGE = "hasImage"
        private const val NOTE_IMAGE = "image"
        private const val NOTE_FOLDER_ID = "folderId"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        val createFolderTable = ("CREATE TABLE " + TABLE_FOLDER + "("
                + FOLDER_ID + " INTEGER PRIMARY KEY," + FOLDER_NAME + " TEXT)")
        db?.execSQL(createFolderTable)

        val createNoteTable = ("CREATE TABLE " + TABLE_NOTE + "("
                + NOTE_ID + " INTEGER PRIMARY KEY UNIQUE," + NOTE_TITLE + " TEXT,"
                + NOTE_CONTENT + " TEXT," + NOTE_CREATED + " TEXT," + NOTE_UPDATED + " TEXT," + HAS_IMAGE + " INTEGER," + NOTE_IMAGE + " TEXT," + NOTE_FOLDER_ID + " INTEGER)")
        db?.execSQL(createNoteTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_FOLDER")
        onCreate(db)
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTE")
        onCreate(db)
    }

    fun viewFolder() : ArrayList<Folder> {
        val folderList = ArrayList<Folder>()
        val allQuery = "SELECT * FROM $TABLE_FOLDER ORDER BY $FOLDER_NAME COLLATE NOCASE ASC"
        val db = this.readableDatabase
        val cursor: Cursor

        try {
            cursor = db.rawQuery(allQuery, null)
        } catch (e: SQLException) {
            db.execSQL(allQuery)
            return ArrayList()
        }

        var id: Int
        var name: String

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(FOLDER_ID))
                name = cursor.getString(cursor.getColumnIndex(FOLDER_NAME))

                val eachFolder = Folder(id=id, name=name)
                folderList.add(eachFolder)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return folderList
    }

    fun addFolder(folder: Folder): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(FOLDER_NAME, folder.name)

        val success = db.insert(TABLE_FOLDER, null, contentValues)
        db.close()
        return success
    }

    fun updateFolder(folder: Folder): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(FOLDER_NAME, folder.name)

        val success = db.update(TABLE_FOLDER, contentValues, FOLDER_ID + "=" + folder.id, null)
        db.close()
        return success
    }

    fun deleteFolder(folder: Folder): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(FOLDER_ID, folder.id)

        val success = db.delete(TABLE_FOLDER, FOLDER_ID + "=" + folder.id, null)
        db.close()
        return success
    }

    fun viewNote(noteFolderId: Int) : ArrayList<Note> {
        val noteList = ArrayList<Note>()
        val allQuery = "SELECT * FROM $TABLE_NOTE WHERE $NOTE_FOLDER_ID=$noteFolderId ORDER BY $NOTE_TITLE COLLATE NOCASE ASC"
        val db = this.readableDatabase
        val cursor: Cursor

        try {
            cursor = db.rawQuery(allQuery, null)
        } catch (e: SQLException) {
            db.execSQL(allQuery)
            return ArrayList()
        }

        var id: Int
        var title: String
        var content: String
        var created: String
        var updated: String
        var hasImage: Int
        var image: String
        var folderId: Int

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(NOTE_ID))
                title = cursor.getString(cursor.getColumnIndex(NOTE_TITLE))
                content = cursor.getString(cursor.getColumnIndex(NOTE_CONTENT))
                created = cursor.getString(cursor.getColumnIndex(NOTE_CREATED))
                updated = cursor.getString(cursor.getColumnIndex(NOTE_UPDATED))
                hasImage = cursor.getInt(cursor.getColumnIndex(HAS_IMAGE))
                image = if (cursor.getString(cursor.getColumnIndex(NOTE_IMAGE)) != null) cursor.getString(cursor.getColumnIndex(NOTE_IMAGE))
                        else "image"
                folderId = cursor.getInt(cursor.getColumnIndex(NOTE_FOLDER_ID))

                val eachNote = Note(id=id, title=title, content=content, created=created, updated=updated, hasImage=hasImage, image=image, folderId=folderId)
                noteList.add(eachNote)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return noteList
    }

    fun addNote(note: Note): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(NOTE_TITLE, note.title)
        contentValues.put(NOTE_CONTENT, note.content)
        contentValues.put(NOTE_CREATED, note.created)
        contentValues.put(NOTE_UPDATED, note.updated)
        contentValues.put(HAS_IMAGE, note.hasImage)
        contentValues.put(NOTE_IMAGE, note.image)
        contentValues.put(NOTE_FOLDER_ID, note.folderId)

        val success = db.insert(TABLE_NOTE, null, contentValues)
        db.close()
        return success
    }

    fun updateNote(note: Note): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(NOTE_TITLE, note.title)
        contentValues.put(NOTE_CONTENT, note.content)
        contentValues.put(NOTE_UPDATED, note.updated)
        contentValues.put(HAS_IMAGE, note.hasImage)
        contentValues.put(NOTE_IMAGE, note.image)
        contentValues.put(NOTE_FOLDER_ID, note.folderId)

        val success = db.update(TABLE_NOTE, contentValues, NOTE_ID + "=" + note.id, null)
        db.close()
        return success
    }

    fun deleteNote(note: Note): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(NOTE_ID, note.id)

        val success = db.delete(TABLE_NOTE, NOTE_ID + "=" + note.id, null)
        db.close()
        return success
    }
}