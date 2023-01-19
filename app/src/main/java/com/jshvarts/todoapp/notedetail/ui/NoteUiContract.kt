package com.jshvarts.todoapp.notedetail.ui

import android.os.Parcelable
import com.jshvarts.todoapp.arch.UiAction
import com.jshvarts.todoapp.arch.UiEffect
import com.jshvarts.todoapp.arch.UiState
import com.jshvarts.todoapp.data.Note
import kotlinx.parcelize.Parcelize

sealed interface NoteDetailUiAction : UiAction {
  data class LoadNote(val id: String) : NoteDetailUiAction
}

@Parcelize
sealed class NoteDetailUiState : UiState, Parcelable {
  object Loading : NoteDetailUiState()

  data class Success(
    val note: Note
  ) : NoteDetailUiState()

  data class Error(
    val throwable: Throwable? = null
  ) : NoteDetailUiState()
}

sealed interface NoteDetailUiEffect : UiEffect {
  object RequestSubmitted : NoteDetailUiEffect
}
