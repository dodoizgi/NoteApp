package com.task.noteapp

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.task.noteapp.data.database.NoteDao
import com.task.noteapp.data.database.NoteDatabase
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NoteTests {

    private lateinit var noteDatabase: NoteDatabase
    private lateinit var noteDao: NoteDao

    @Before
    fun setup() {
        noteDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            NoteDatabase::class.java
        ).build()
        noteDao = noteDatabase.getNoteDao()
    }

    @Test
    fun afterInsertingOneNote_mustSeeThatNote() = runBlocking {
        noteDao.insertData(
            com.task.noteapp.domain.model.Note(
                1,
                "testTitle",
                "TestDescription",
                "url",
                "02-03-2024",
                false
            )
        )

        val notes = noteDao.getAllDataForTest()
        assertEquals(1, notes.size)
        assertEquals("testTitle", notes[0].title)
    }

    @Test
    fun afterDeletingOneNote_mustSeeThatNote() = runBlocking {

        val testNote = com.task.noteapp.domain.model.Note(
            1,
            "testTitle",
            "TestDescription",
            "url",
            "02-03-2024",
            false
        )
        noteDao.insertData(testNote)

        var notes = noteDao.getAllDataForTest()
        assertEquals(1, notes.size)
        assertEquals("testTitle", notes[0].title)
        noteDao.deleteItem(testNote)
        notes = noteDao.getAllDataForTest()
        assertEquals(0, notes.size)
    }

    @Test
    fun afterUpdateOneNote_mustSeeThatNote() = runBlocking {

        val testNote = com.task.noteapp.domain.model.Note(
            1,
            "testTitle",
            "TestDescription",
            "url",
            "02-03-2024",
            false
        )
        noteDao.insertData(testNote)

        var notes = noteDao.getAllDataForTest()
        assertEquals(1, notes.size)
        assertEquals("testTitle", notes[0].title)

        val testUpdateNote = com.task.noteapp.domain.model.Note(
            1,
            "testUpdateTitle",
            "TestUpdateDescription",
            "urlUpdate",
            "02-03-2024",
            true
        )

        noteDao.updateData(testUpdateNote)
        notes = noteDao.getAllDataForTest()
        assertEquals(1, notes.size)
        assertEquals("testUpdateTitle", notes[0].title)
        assertEquals("TestUpdateDescription", notes[0].description)
        assertEquals("urlUpdate", notes[0].url)
        assertEquals("02-03-2024", notes[0].createdDate)
        assertEquals(true, notes[0].isChanged)

    }

    @After
    fun tearDown() {
        noteDatabase.close()
    }
}