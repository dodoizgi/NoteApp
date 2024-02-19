package com.task.noteapp.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.task.noteapp.data.repository.NoteRepository
import com.task.noteapp.domain.model.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: NoteRepository

) : ViewModel() {
    val getAllData: LiveData<List<Note>> = repository.getAllData

    fun insertData(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertData(note)
        }
    }

    fun updateData(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateData(note)
        }
    }

    fun deleteItem(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteItem(note)
        }
    }
}