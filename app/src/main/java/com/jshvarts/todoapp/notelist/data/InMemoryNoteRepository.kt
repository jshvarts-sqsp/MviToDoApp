package com.jshvarts.todoapp.notelist.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class InMemoryNoteRepository @Inject constructor() : NoteRepository {
  override fun getNotes(): Flow<List<Note>> = flow {
    val noteList = listOf(
      Note(
        text = "Note 1"
      ),
      Note(
        text = "Note 2"
      ),
      Note(
        text = "Note 3"
      ),
      Note(
        text = "Note 4"
      )
    )
    emit(noteList.shuffled())
  }
}