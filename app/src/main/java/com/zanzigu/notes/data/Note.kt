package com.zanzigu.notes.data
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note (
    @PrimaryKey(autoGenerate = true) val id: Int,
    var title: String,
    var pos: Int,
    var categoryID: Int
)

@Entity
data class NotesCategory(
    @PrimaryKey(autoGenerate = true) val categoryID: Int,
    var name: String,
    var categoryPos: Int
)