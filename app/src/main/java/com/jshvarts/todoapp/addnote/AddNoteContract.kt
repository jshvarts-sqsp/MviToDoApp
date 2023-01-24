package com.jshvarts.todoapp.addnote

import com.jshvarts.todoapp.arch.Action
import com.jshvarts.todoapp.arch.Effect

sealed interface AddNoteAction : Action {
  data class SaveNote(val title: String) : AddNoteAction
}

sealed interface AddNoteEffect : Effect {
  object SaveNoteFailure : AddNoteEffect
  object SaveNoteSuccess : AddNoteEffect
}
