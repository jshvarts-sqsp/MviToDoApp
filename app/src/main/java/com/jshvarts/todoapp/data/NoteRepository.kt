package com.jshvarts.todoapp.data

import kotlinx.coroutines.flow.Flow

interface NoteRepository {
  fun getNotes(): Flow<List<Note>>
  fun getNote(id: Int): Flow<Note>
  suspend fun refreshNotes(): Result<Unit>
  suspend fun deleteNote(id: Int): Result<Unit>
  suspend fun addNote(title: String): Result<Unit>
  suspend fun updateNote(note: Note): Result<Unit>
}