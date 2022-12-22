package com.zanzigu.notes.data

import androidx.lifecycle.LiveData

class NoteRepository(private val noteDao: NoteDao) {
    val getNotes: LiveData<List<Note>> = noteDao.getNotes()
    val getCategories: LiveData<List<NotesCategory>> = noteDao.getCategories()

    suspend fun addNote(note: Note) {
        noteDao.addNote(note)
    }
    suspend fun updateNote(note: Note) {
        noteDao.updateNote(note)
    }
    suspend fun updateNotes(notes: List<Note>) {
        for (note in notes) {
            noteDao.updateNote(note)
        }
    }
    suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note)
    }

    suspend fun addCategory(category: NotesCategory) {
        noteDao.addCategory(category)
    }
    suspend fun updateCategory(category: NotesCategory) {
        noteDao.updateCategory(category)
    }
    suspend fun updateCategories(newCategories: List<NotesCategory>) {
        for (category in newCategories) {
            noteDao.updateCategory(category)
        }
    }
    suspend fun deleteCategory(category: NotesCategory) {
        noteDao.deleteCategory(category)
        noteDao.deleteNotesByCategoryID(category.categoryID)
    }
}