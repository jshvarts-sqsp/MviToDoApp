package com.jshvarts.todoapp.addnote

import android.os.Parcelable
import com.jshvarts.todoapp.arch.Action
import com.jshvarts.todoapp.arch.Effect
import com.jshvarts.todoapp.arch.State
import kotlinx.parcelize.Parcelize

sealed interface AddNoteAction : Action {
  data class SaveNote(val title: String) : AddNoteAction
  data class WriteNote(val title: String) : AddNoteAction
}

@Parcelize
data class AddNodeState(
  val title: String = "",
  val saveEnabled: Boolean = false
) : State, Parcelable

sealed interface AddNoteEffect : Effect {
  object SaveNoteFailure : AddNoteEffect
  object SaveNoteSuccess : AddNoteEffect
}
