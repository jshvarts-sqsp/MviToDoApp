package com.jshvarts.todoapp.notedetail.ui

import android.os.Parcelable
import com.jshvarts.todoapp.arch.UiAction
import com.jshvarts.todoapp.arch.UiEffect
import com.jshvarts.todoapp.arch.UiState
import com.jshvarts.todoapp.data.Note
import kotlinx.parcelize.Parcelize

sealed interface NoteDetailUiAction : UiAction {
  data class LoadNote(
    val id: Int,
    val forEditing: Boolean = false
  ) : NoteDetailUiAction

  data class SaveNote(
    val id: Int,
    val title: String,
    val completed: Boolean
  ) : NoteDetailUiAction

  data class DeleteNote(val id: Int) : NoteDetailUiAction
}

@Parcelize
sealed class NoteDetailUiState : UiState, Parcelable {
  object Loading : NoteDetailUiState()

  data class Success(
    val note: Note,
    val forEditing: Boolean = false
  ) : NoteDetailUiState()

  data class Error(
    val throwable: Throwable? = null
  ) : NoteDetailUiState()
}

sealed interface NoteDetailUiEffect : UiEffect {
  object EditFailure : NoteDetailUiEffect
  object EditSuccess : NoteDetailUiEffect
  object DeleteFailure : NoteDetailUiEffect
  object DeleteSuccess : NoteDetailUiEffect
}
