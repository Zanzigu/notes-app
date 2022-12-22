package com.zanzigu.notes.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteDao {
    // notes
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query(value = "DELETE FROM note WHERE categoryID=:categoryID")
    suspend fun deleteNotesByCategoryID(categoryID: Int)

    @Query(value = "SELECT * FROM note ORDER BY pos ASC")
    fun getNotes(): LiveData<List<Note>>

    // categories
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addCategory(category: NotesCategory)

    @Update
    suspend fun updateCategory(category: NotesCategory)

    @Delete
    suspend fun deleteCategory(category: NotesCategory)

    @Query(value = "SELECT * FROM notescategory ORDER BY categoryPos ASC")
    fun getCategories(): LiveData<List<NotesCategory>>
}