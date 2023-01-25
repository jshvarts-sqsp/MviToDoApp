package com.jshvarts.todoapp.notedetail

import android.os.Parcelable
import com.jshvarts.todoapp.arch.Action
import com.jshvarts.todoapp.arch.Effect
import com.jshvarts.todoapp.arch.State
import com.jshvarts.todoapp.data.Note
import kotlinx.parcelize.Parcelize

sealed interface NoteDetailAction : Action {
  data class LoadNote(
    val id: Int,
    val forEditing: Boolean = false
  ) : NoteDetailAction

  data class EditNote(
    val id: Int,
    val title: String,
    val completed: Boolean,
    val forEditing: Boolean = false
  ) : NoteDetailAction

  data class SaveNote(
    val id: Int,
    val title: String,
    val completed: Boolean
  ) : NoteDetailAction

  data class DeleteNote(val id: Int) : NoteDetailAction
}

@Parcelize
sealed class NoteDetailState : State, Parcelable {
  object Loading : NoteDetailState()

  data class Success(
    val note: Note
  ) : NoteDetailState()

  data class Error(
    val throwable: Throwable? = null
  ) : NoteDetailState()

  data class EditNote(
    val id: Int,
    val title: String,
    val completed: Boolean,
    val saveEnabled: Boolean
  ) : NoteDetailState()
}

sealed interface NoteDetailEffect : Effect {
  object EditFailure : NoteDetailEffect
  object EditSuccess : NoteDetailEffect
  object DeleteFailure : NoteDetailEffect
  object DeleteSuccess : NoteDetailEffect
}
