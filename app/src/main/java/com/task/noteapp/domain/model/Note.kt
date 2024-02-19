package com.task.noteapp.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_table")
data class Note(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var title: String,
    var description: String,
    var url: String?,
    var createdDate: String,
    var isChanged: Boolean
)
