package com.jshvarts.todoapp.notelist.ui

import com.jshvarts.todoapp.arch.UiMapper
import com.jshvarts.todoapp.notelist.data.Note
import javax.inject.Inject

class UiNoteMapper @Inject constructor() : UiMapper<Note, UiNote> {
  override fun toUi(note: Note): UiNote {
    return UiNote(note.text)
  }
}