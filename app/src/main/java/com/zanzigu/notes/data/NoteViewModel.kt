package com.zanzigu.notes.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel(application: Application): AndroidViewModel(application) {
    val getNotes: LiveData<List<Note>>
    val getCategories: LiveData<List<NotesCategory>>
    private val repository: NoteRepository

    init {
        val noteDao = NoteDatabase.getDatabase(application).noteDao()
        repository = NoteRepository(noteDao)
        getNotes = repository.getNotes
        getCategories = repository.getCategories
    }

    fun addNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addNote(note)
        }
    }
    fun updateNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateNote(note)
        }
    }
    fun updateNotes(notes: List<Note>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateNotes(notes)
        }
    }
    fun deleteNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteNote(note)
        }
    }

    fun addCategory(category: NotesCategory) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addCategory(category)
        }
    }
    fun updateCategory(category: NotesCategory) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCategory(category)
        }
    }
    fun updateCategories(newCategories: List<NotesCategory>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCategories(newCategories)
        }
    }
    fun deleteCategory(category: NotesCategory) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCategory(category)
        }
    }
}