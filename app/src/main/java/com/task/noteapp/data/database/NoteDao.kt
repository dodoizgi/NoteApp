package com.task.noteapp.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.task.noteapp.domain.model.Note

@Dao
interface NoteDao {

    @Query("SELECT * FROM note_table ORDER BY id ASC")
    fun getAllData(): LiveData<List<Note>>

    @Query("SELECT * FROM note_table")
    fun getAllDataForTest(): List<Note>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertData(note: Note)

    @Update
    suspend fun updateData(note: Note)

    @Delete
    suspend fun deleteItem(note: Note)

    @Query("DELETE FROM note_table")
    suspend fun deleteAll()
}