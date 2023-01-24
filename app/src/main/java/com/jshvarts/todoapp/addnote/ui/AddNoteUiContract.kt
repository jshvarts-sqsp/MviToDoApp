package com.jshvarts.todoapp.addnote.ui

import com.jshvarts.todoapp.arch.UiAction
import com.jshvarts.todoapp.arch.UiEffect

sealed interface AddNoteUiAction : UiAction {
  data class SaveNote(val title: String) : AddNoteUiAction
}

sealed interface AddNoteUiEffect : UiEffect {
  object SaveNoteFailure : AddNoteUiEffect
  object SaveNoteSuccess : AddNoteUiEffect
}
