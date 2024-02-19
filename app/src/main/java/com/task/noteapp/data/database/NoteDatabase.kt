package com.task.noteapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.task.noteapp.domain.model.Note

@Database(entities = [Note::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun getNoteDao(): NoteDao
}