package com.jshvarts.todoapp.notelist.data

import kotlinx.coroutines.flow.Flow

interface NoteRepository {
  fun getNotes(): Flow<List<Note>>
}