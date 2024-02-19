package com.task.noteapp.di

import android.content.Context
import androidx.room.Room
import com.task.noteapp.data.database.NoteDao
import com.task.noteapp.data.database.NoteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NoteDatabase {
        return Room.databaseBuilder(
            context,
            NoteDatabase::class.java,
            "note_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideNoteDao(database: NoteDatabase): NoteDao {
        return database.getNoteDao()
    }
}