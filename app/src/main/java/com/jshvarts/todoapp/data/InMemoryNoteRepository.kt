package com.jshvarts.todoapp.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class InMemoryNoteRepository @Inject constructor() : NoteRepository {
  override fun getNotes(): Flow<List<Note>> = flow {
    val noteList = listOf(
      Note(
        id = "1",
        text = "Note 1"
      ),
      Note(
        id = "2",
        text = "Note 2"
      ),
      Note(
        id = "3",
        text = "Note 3"
      ),
      Note(
        id = "4",
        text = "Note 4"
      )
    )
    emit(noteList.shuffled())
  }

  override fun getNote(id: String): Flow<Note> {
    return flow {
      when(id) {
        "1" -> Note(
          id = "1",
          text = "Note 1"
        )
        "2" -> Note(
          id = "2",
          text = "Note 2"
        )
        "3" -> Note(
          id = "3",
          text = "Note 3"
        )
        "4" -> Note(
          id = "4",
          text = "Note 4"
        )
      }
    }
  }
}