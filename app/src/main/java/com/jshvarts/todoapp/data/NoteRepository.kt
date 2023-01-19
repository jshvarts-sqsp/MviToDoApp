package com.jshvarts.todoapp.data

import kotlinx.coroutines.flow.Flow

interface NoteRepository {
  fun getNotes(): Flow<List<Note>>
  fun getNote(id: String): Flow<Note>
}